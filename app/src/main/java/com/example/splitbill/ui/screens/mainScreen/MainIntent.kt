package com.example.splitbill.ui.screens.mainScreen

import com.example.splitbill.data.local.entity.Friend

sealed class MainIntent {
    object AddFriend : MainIntent()
    data class EditFriend(val id: Long?, val newName: String) : MainIntent()
    data class DeleteFriend(val friend:Friend) : MainIntent()
    object OnAddBillClicked  : MainIntent()
    object FetchFriends : MainIntent()
    object FetchBills : MainIntent()
    data class OnNewFriendNameChange(val name: String) : MainIntent()
    object OpenAddFriendDialog : MainIntent()
    object CloseAddFriendDialog : MainIntent()
    data class OpenEditFriendDialog(val friend:Friend) : MainIntent()
    object CloseEditFriendDialog : MainIntent()
    object ShowDeleteDialog : MainIntent()
    object DismissDeleteDialog : MainIntent()
}

