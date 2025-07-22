package com.example.splitbill.ui.Screens.MainScreen

import com.example.splitbill.data.local.entity.Friend

sealed class MainIntent {
    object LoadAll : MainIntent()



    object AddFriend : MainIntent()
    data class EditFriend(val friend:Friend) : MainIntent()
    data class DeleteFriend(val friend:Friend) : MainIntent()

    object OnAddBillClicked  : MainIntent()
    object FetchFriends : MainIntent()


    data class OnNewFriendNameChange(val name: String) : MainIntent()
    object OpenAddFriendDialog : MainIntent()
    object CloseAddFriendDialog : MainIntent()

    data class OpenEditFriendDialog(val friend:Friend) : MainIntent()
    object CloseEditFriendDialog : MainIntent()
}

