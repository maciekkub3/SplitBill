package com.example.splitbill.ui.Screens.AddBillScreen

import com.example.splitbill.data.local.entity.Friend


data class AddBillUiState(
    val title: String = "",
    val friends: List<Friend> = emptyList(),
    val selectedFriendIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)
