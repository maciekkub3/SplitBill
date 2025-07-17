package com.example.splitbill.ui.Screens.AddBillScreen

import com.example.splitbill.data.local.entity.Bill
import com.example.splitbill.data.local.entity.Friend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class AddBillUiState(
    val title: String = "",
    val friends: List<Friend> = emptyList(),
    val selectedFriendIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)
