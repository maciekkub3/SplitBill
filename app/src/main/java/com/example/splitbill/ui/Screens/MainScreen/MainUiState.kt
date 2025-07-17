package com.example.splitbill.ui.Screens.MainScreen

import com.example.splitbill.data.local.entity.Bill
import com.example.splitbill.data.local.entity.Friend

data class MainUiState(
    val bills: List<Bill> = emptyList(),
    val friends: List<Friend> = emptyList(),
    val addFriendDialog: Boolean = false,
    val editFriendDialog: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
