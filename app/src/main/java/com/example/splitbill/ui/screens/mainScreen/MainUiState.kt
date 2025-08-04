package com.example.splitbill.ui.screens.mainScreen

import com.example.splitbill.data.classes.BillWithParticipantCount
import com.example.splitbill.data.local.entity.Friend

data class MainUiState(
    val bills: List<BillWithParticipantCount> = emptyList(),
    val friends: List<Friend> = emptyList(),
    val addFriendDialog: Boolean = false,
    val editFriendDialog: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val friendName: String = "",
    val friendImageUri: String? = null,
    val editingFriendId: Long? = null,
    val showDeleteDialog: Boolean = false,
)
