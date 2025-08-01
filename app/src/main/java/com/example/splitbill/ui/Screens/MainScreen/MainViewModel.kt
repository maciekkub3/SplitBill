package com.example.splitbill.ui.Screens.MainScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbill.data.local.entity.Friend
import com.example.splitbill.data.repository.BillParticipantRepository
import com.example.splitbill.data.repository.BillRepository
import com.example.splitbill.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.splitbill.data.classes.BillWithParticipantCount
import com.example.splitbill.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val friendRepository: FriendRepository,
    private val billParticipantRepository: BillParticipantRepository,
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state

    private val _effect = MutableSharedFlow<MainEffect>()
    val effect: SharedFlow<MainEffect> = _effect

    fun handleIntent(intent: MainIntent) {
        when (intent) {
            MainIntent.AddFriend -> addFriend()
            MainIntent.CloseAddFriendDialog -> closeAddFriendDialog()
            MainIntent.OpenAddFriendDialog -> openAddFriendDialog()
            MainIntent.OnAddBillClicked -> onAddBillClicked()
            MainIntent.FetchFriends -> fetchFriends()
            MainIntent.FetchBills -> fetchBillsWithParticipants()
            MainIntent.CloseEditFriendDialog -> closeEditFriendDialog()
            is MainIntent.OnNewFriendNameChange -> onNewFriendNameChange(intent.name)
            is MainIntent.DeleteFriend -> deleteFriend(intent.friend)
            is MainIntent.EditFriend -> editFriendName(intent.id, intent.newName)
            is MainIntent.OpenEditFriendDialog -> openEditFriendDialog(intent.friend)
        }
    }

    private fun addFriend() {
        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {
                val friend = Friend(name = _state.value.friendName)
                friendRepository.upsertFriend(friend)

                _state.value = _state.value.copy(isLoading = false, addFriendDialog = false)

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error saving friend", e)

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun deleteFriend(friend: Friend) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                friendRepository.deleteFriend(friend)

                _state.value = _state.value.copy(isLoading = false, editFriendDialog = false)

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error deleting friend", e)

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun editFriendName(id: Long?, newName: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                friendRepository.updateFriendName(id, newName)

                _state.value = _state.value.copy(isLoading = false, editFriendDialog = false)

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error editing friend", e)

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun fetchBillsWithParticipants() {
        viewModelScope.launch {
            try {
                combine(
                    billRepository.allBills(),
                    billParticipantRepository.getAllParticipants(),
                ) { bills, participants ->

                    val countMap = participants.groupingBy { it.billId }.eachCount()

                    val totalAmounts = expenseRepository.getTotalAmountPerBill()
                        .associateBy({ it.billId }, { it.totalAmount })

                    bills.map { bill ->
                        BillWithParticipantCount(
                            id = bill.id,
                            title = bill.title,
                            createdAt = bill.createdAt,
                            participantCount = countMap[bill.id] ?: 0,
                            totalAmount = totalAmounts[bill.id] ?: 0.0
                        )
                    }
                }.collect { billsWithCount ->
                    _state.value = _state.value.copy(bills = billsWithCount)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching bills", e)
                _state.value = _state.value.copy(error = e.message ?: "Unknown error")
            }
        }
    }

    fun fetchFriends() {
        viewModelScope.launch {
            try {
                friendRepository.getFriends().collectLatest { friendList ->
                    _state.value = _state.value.copy(
                        friends = friendList,
                        isLoading = false,
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching friends", e)
                _state.value = _state.value.copy(error = e.message ?: "Unknown error")
            }
        }
    }

    private fun onNewFriendNameChange(name: String) {
        _state.value = _state.value.copy(friendName = name)
    }

    private fun onAddBillClicked() {
        viewModelScope.launch {
            _effect.emit(MainEffect.NavigateToBillScreen)
        }
    }

    private fun closeAddFriendDialog() {
            _state.value = _state.value.copy(addFriendDialog = false)
    }

    private fun closeEditFriendDialog() {
            _state.value = _state.value.copy(editFriendDialog = false,)
    }

    private fun openEditFriendDialog(friend: Friend) {
            _state.value = _state.value.copy( editFriendDialog = true, friendName = friend.name, editingFriendId = friend.id)
    }
    private fun openAddFriendDialog() {
            _state.value = _state.value.copy(addFriendDialog = true, friendName = "")
    }
}
