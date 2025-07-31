package com.example.splitbill.ui.Screens.BillScreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.splitbill.data.local.entity.Expense
import com.example.splitbill.data.local.entity.Friend
import com.example.splitbill.data.repository.BillParticipantRepository
import com.example.splitbill.data.repository.BillRepository
import com.example.splitbill.data.repository.ExpenseRepository
import com.example.splitbill.data.repository.FriendRepository
import com.example.splitbill.navigation.EventRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val billRepository: BillRepository,
    private val expenseRepository: ExpenseRepository,
    private val billParticipantRepository: BillParticipantRepository,
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BillUiState())
    val state: StateFlow<BillUiState> = _state

    val args = savedStateHandle.toRoute<EventRoute>()

    fun handleIntent(intent: BillIntent) {
        when (intent) {
            is BillIntent.AddExpense -> addExpense(intent.description, intent.amount, intent.paidById)
            BillIntent.CalculateSettlement -> calculateSettlement()
            BillIntent.DismissSettlementDialog -> dismissSettlementDialog()
            is BillIntent.LoadBill -> fetchBillWithParticipantsAndExpenses(args.billId)
            BillIntent.DismissAddExpenseDialog -> dismissAddExpenseDialog()
            BillIntent.DismissEditExpenseDialog -> dismissEditExpenseDialog()
            BillIntent.ShowAddExpenseDialog -> showAddExpenseDialog()
            BillIntent.ShowEditExpenseDialog -> showEditExpenseDialog()
            is BillIntent.AmountChange -> amountChange(intent.amount)
            is BillIntent.DescriptionChange -> descriptionChange(intent.description)
            is BillIntent.PayerChange -> payerChange(intent.payer)
            is BillIntent.DeleteBill -> deleteBill()
        }
    }

    fun deleteBill() {
        viewModelScope.launch {
            try {
                billRepository.deleteBill(_state.value.bill ?: return@launch)
                _state.value = _state.value.copy(bill = null, expenses = emptyList(), friends = emptyList())
            } catch (e: Exception) {
                Log.e("BillViewModel", "Error deleting bill: ${e.message}")
            }
        }
    }

    fun dismissSettlementDialog() { _state.value = _state.value.copy(showSettleUpDialog = false, settlementResult = emptyList()) }

    private fun calculateSettlement() {
        val expenses = _state.value.expenses
        val friends = _state.value.friends

        if (expenses.isEmpty() || friends.isEmpty()) return

        val total = expenses.sumOf { it.amount }
        val perPerson = total / friends.size

        // Map from friend id to amount paid
        val paidMap = mutableMapOf<Long, Double>().apply {
            friends.forEach { put(it.id, 0.0) }
            expenses.forEach { exp ->
                exp.paidById?.let { put(it, (get(it) ?: 0.0) + exp.amount) }
            }
        }

        // Balance = how much each friend owes (+ve means creditor, -ve means debtor)
        val balance = paidMap.mapValues { it.value - perPerson }.toMutableMap()

        // Cache friend names by ID to avoid repeated searching
        val friendNameById = friends.associateBy({ it.id }, { it.name })

        // Separate creditors and debtors
        val creditors = balance.filter { it.value > 0.01 }.map { it.key to it.value }.toMutableList()
        val debtors = balance.filter { it.value < -0.01 }.map { it.key to it.value }.toMutableList()

        val result = mutableListOf<SettlementEntry>()

        var i = 0
        var j = 0
        var iterations = 0
        val maxIterations = 10_000  // safety limit to prevent infinite loops

        while (i < debtors.size && j < creditors.size && iterations < maxIterations) {
            val debtorId = debtors[i].first
            val creditorId = creditors[j].first

            val debt = -balance[debtorId]!!
            val credit = balance[creditorId]!!

            val amount = minOf(debt, credit)

            if (amount > 0.01) {
                result.add(
                    SettlementEntry(
                        from = friendNameById[debtorId] ?: "Unknown",
                        to = friendNameById[creditorId] ?: "Unknown",
                        amount = amount
                    )
                )
            }

            // Update balances
            balance[debtorId] = balance[debtorId]!! + amount
            balance[creditorId] = balance[creditorId]!! - amount

            // Move to next debtor or creditor if settled (within tolerance)
            if (balance[debtorId]!! >= -0.01) i++
            if (balance[creditorId]!! <= 0.01) j++

            iterations++
        }

        if (iterations == maxIterations) {
            Log.w("BillViewModel", "Settlement calculation stopped early after $maxIterations iterations")
        }

        _state.value = _state.value.copy(
            settlementResult = result,
            showSettleUpDialog = true
        )
    }


    fun addExpense(description: String,amount: Double,paidById: Long) {
        viewModelScope.launch {
            try {
                expenseRepository.upsertExpense(
                    Expense(
                        name = description,
                        amount = amount,
                        paidById = paidById,
                        billId = args.billId
                    )
                )
                _state.value = _state.value.copy(showAddExpenseDialog = false, description = "", amount = "", payer = null)
                fetchBillWithParticipantsAndExpenses(args.billId)
            } catch (e: Exception) {
                Log.e("BillViewModel", "Error adding expense: ${e.message}")
            }
        }
    }

    fun amountChange(amount: String) { _state.value = _state.value.copy(amount = amount) }

    fun descriptionChange(description: String) { _state.value = _state.value.copy(description = description) }

    fun payerChange(payer: Friend?) { _state.value = _state.value.copy(payer = payer) }

    fun showAddExpenseDialog() { _state.value = _state.value.copy(showAddExpenseDialog = true, description = "", amount = "", payer = null) }

    fun dismissAddExpenseDialog() { _state.value = _state.value.copy(showAddExpenseDialog = false) }

    fun showEditExpenseDialog() { _state.value = _state.value.copy(showEditExpenseDialog = true) }

    fun dismissEditExpenseDialog() { _state.value = _state.value.copy(showEditExpenseDialog = false) }

    fun fetchBillWithParticipantsAndExpenses(billId: Long) {
        viewModelScope.launch {
            try {
                val bill = billRepository.getById(billId)
                if (bill != null) {

                    val participants = billParticipantRepository.getParticipantsForBill(billId)
                    val expenses = expenseRepository.getExpensesForBill(billId)
                    val friendIds = participants.map { it.friendId }.distinct()
                    val allFriends = friendRepository.getFriendsByIds(friendIds)
                    val totalAmount = expenses.sumOf { it.amount }

                    _state.value = _state.value.copy(
                        bill = bill,
                        friends = allFriends,
                        expenses = expenses,
                        totalAmount = totalAmount,
                    )
                } else {
                    Log.e("BillViewModel", "Bill with ID $billId not found")
                }
            } catch (e: Exception) {
                Log.e("BillViewModel", "Error fetching bill data: ${e.message}")
            }
        }
    }
}
