package com.example.splitbill.ui.screens.addBillScreen

sealed class AddBillIntent {
    data class EnterTitle(val title: String) : AddBillIntent()
    data class ToggleParticipant(val friendId: Long) : AddBillIntent()
    object SaveBill : AddBillIntent()
    object FetchFriends : AddBillIntent()
}

