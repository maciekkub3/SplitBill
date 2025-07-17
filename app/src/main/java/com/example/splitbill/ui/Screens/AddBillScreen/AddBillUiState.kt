package com.example.splitbill.ui.Screens.AddBillScreen

import com.example.splitbill.data.local.entity.Friend

data class AddBillUiState(
    val title: String = "",
    val participants: List<Friend> = emptyList(),
    val selectedFriendIds: Set<Int> = emptySet(),
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)
