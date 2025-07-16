package com.example.splitbill.ui.Screens.AddBillScreen

sealed class AddBillIntent {
    data class EnterTitle(val title: String) : AddBillIntent()
    data class ToggleParticipant(val friendId: Int) : AddBillIntent()
    object SaveBill : AddBillIntent()
}
