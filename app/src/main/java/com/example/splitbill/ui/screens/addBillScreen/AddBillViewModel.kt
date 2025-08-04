package com.example.splitbill.ui.screens.addBillScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbill.data.local.entity.Bill
import com.example.splitbill.data.repository.BillRepository
import com.example.splitbill.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBillViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val friendRepository: FriendRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddBillUiState())
    val state: StateFlow<AddBillUiState> = _state

    private val _effect = MutableSharedFlow<AddBillEffect>()
    val effect: SharedFlow<AddBillEffect> = _effect

    fun handleIntent(intent: AddBillIntent) {
        when (intent) {
            is AddBillIntent.EnterTitle -> enterTitle(intent.title)
            AddBillIntent.SaveBill -> handleSaveBill()
            is AddBillIntent.ToggleParticipant -> toggleParticipant(intent.friendId)
            AddBillIntent.FetchFriends -> fetchFriends()
        }
    }

    private fun fetchFriends() {
        viewModelScope.launch {
            try {
                friendRepository.getFriends().collectLatest { friendList ->
                    _state.update {
                        it.copy(
                            friends = friendList,
                            isLoading = false,
                        )
                    }
                }

            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Unknown error") }
            }
        }
    }


    private fun toggleParticipant(friendId: Long) {
        val selectedFriendIds = _state.value.selectedFriendIds.toMutableSet()
        if (selectedFriendIds.contains(friendId)) {
            selectedFriendIds.remove(friendId)
            _state.update { it.copy(selectedFriendIds = selectedFriendIds) }
        } else {
            selectedFriendIds.add(friendId)
            _state.update { it.copy(selectedFriendIds = selectedFriendIds) }
        }
    }

    private fun enterTitle(title: String) {
        _state.update {
            it.copy(
                title = title
            )
        }
    }

    private fun validateBill(): Boolean {

        val title = _state.value.title.trim()
        val selectedFriends = _state.value.selectedFriendIds

        val titleError = if (title.isBlank()) "Bill name cannot be empty" else null
        val participantsError = if (selectedFriends.isEmpty()) "At least one participant must be selected" else null

        val hasError = listOf(titleError, participantsError).any { it != null }

        if (hasError) {
            _state.update {
                it.copy(
                    billNameError = titleError,
                    participantsError = participantsError
                )
            }
            return false
        }
        return true
    }

    private fun handleSaveBill() {
        if (!validateBill()) return
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true) }

            try {
                val bill = Bill(title = _state.value.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
                val participants = _state.value.selectedFriendIds
                billRepository.insertBillAndParticipants(bill, participants.toList())
                _effect.emit(AddBillEffect.NavigateToBillScreen)

            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
                    }
            }
        }
    }
}
