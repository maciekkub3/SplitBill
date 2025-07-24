package com.example.splitbill.ui.Screens.AddBillScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbill.data.local.entity.Bill
import com.example.splitbill.data.repository.BillRepository
import com.example.splitbill.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBillViewModel @Inject constructor(

    private val billRepository: BillRepository,
    private val friendRepository: FriendRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddBillUiState())
    val state: StateFlow<AddBillUiState> = _state

    fun handleIntent(intent: AddBillIntent) {
        when (intent) {
            is AddBillIntent.EnterTitle -> enterTitle(intent.title)
            AddBillIntent.SaveBill -> handleSaveBill()
            is AddBillIntent.ToggleParticipant -> toggleParticipant(intent.friendId)
            AddBillIntent.FetchFriends -> fetchFriends()
        }
    }

    fun fetchFriends() {
        viewModelScope.launch {
            try {
                val friends = friendRepository.getFriends().collectLatest { friendList ->
                    _state.value = _state.value.copy(
                        friends = friendList,
                        isLoading = false,
                    )
                }

            } catch (e: Exception) {
                Log.e("AddBillViewModel", "Error fetching friends", e)
                _state.value = _state.value.copy(error = e.message ?: "Unknown error")
            }
        }
    }

    fun toggleParticipant(friendId: Long) {
        val selectedFriendIds = _state.value.selectedFriendIds.toMutableSet()
        if (selectedFriendIds.contains(friendId)) {
            selectedFriendIds.remove(friendId)
            _state.value = _state.value.copy(selectedFriendIds = selectedFriendIds)
        } else {
            selectedFriendIds.add(friendId)
            _state.value = _state.value.copy(selectedFriendIds = selectedFriendIds)
        }

    }

    fun enterTitle(title: String) {
        _state.value = _state.value.copy(
            title = title
        )
    }


    private fun handleSaveBill() {
        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {
                val bill = Bill(title = _state.value.title)
                val participants = _state.value.selectedFriendIds
                billRepository.insertBillAndParticipants(bill, participants.toList())

                _state.value = AddBillUiState(saveSuccess = true)

            } catch (e: Exception) {
                Log.e("AddBillViewModel", "Error saving bill", e)

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

}
