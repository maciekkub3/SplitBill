package com.example.splitbill.ui.Screens.BillScreen

import com.example.splitbill.data.local.entity.Bill
import com.example.splitbill.data.local.entity.BillParticipant
import com.example.splitbill.data.local.entity.Expense
import com.example.splitbill.data.local.entity.Friend

data class BillUiState(
    val bill: Bill? = null,
    val expenses: List<Expense> = emptyList(),
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val settlementResult: List<SettlementEntry> = emptyList(),
    val participants: List<BillParticipant> = emptyList(),
    val friends: List<Friend> = emptyList(),
    val showAddExpenseDialog: Boolean = false,
    val showEditExpenseDialog: Boolean = false,
    val showSettleUpDialog: Boolean = false,
    val payer: Friend? = null,
    val description: String = "",
    val amount: String = "",
)
data class SettlementEntry(
    val from: String,
    val to: String,
    val amount: Double
)
