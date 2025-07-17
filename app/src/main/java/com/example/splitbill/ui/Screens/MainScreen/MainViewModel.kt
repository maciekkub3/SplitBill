package com.example.splitbill.ui.Screens.MainScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbill.data.local.entity.Bill
import com.example.splitbill.data.local.entity.Friend
import com.example.splitbill.data.repository.BillRepository
import com.example.splitbill.data.repository.FriendRepository
import com.example.splitbill.navigation.AddEventRoute
import com.example.splitbill.ui.Screens.AddBillScreen.AddBillIntent
import com.example.splitbill.ui.Screens.AddBillScreen.AddBillUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val friendRepository: FriendRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state

    private val _effect = MutableSharedFlow<MainEffect>()
    val effect: SharedFlow<MainEffect> = _effect


    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.AddFriend -> addFriend()
            MainIntent.CloseAddFriendDialog -> closeAddFriendDialog()
            MainIntent.OpenAddFriendDialog -> openAddFriendDialog()
            is MainIntent.OnAddBillClicked -> onAddBillClicked()
            is MainIntent.OnNewFriendNameChange -> onNewFriendNameChange(intent.name)
            MainIntent.FetchFriends -> fetchFriends()


            MainIntent.CloseEditFriendDialog -> TODO()
            is MainIntent.DeleteFriend -> TODO()
            is MainIntent.EditFriend -> TODO()
            MainIntent.LoadAll -> TODO()
            is MainIntent.OpenEditFriendDialog -> TODO()


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
                Log.e("MainViewModel", "Error fetching friends", e)
                _state.value = _state.value.copy(error = e.message ?: "Unknown error")
            }
        }
    }

    private fun onNewFriendNameChange(name: String) {
        _state.value = _state.value.copy(
            friendName = name
        )
    }

    private fun onAddBillClicked() {
        viewModelScope.launch {
            _effect.emit(MainEffect.NavigateToBillScreen)
        }
    }

    private fun closeAddFriendDialog() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                addFriendDialog = false,
            )
        }
    }

    private fun openAddFriendDialog() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                addFriendDialog = true,
                friendName = ""
            )
        }
    }

    private fun addFriend() {
        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {
                val friend = Friend(name = _state.value.friendName)
                friendRepository.upsertFriend(friend)

                _state.value = MainUiState(isLoading = false)

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error saving friend", e)

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }



}
