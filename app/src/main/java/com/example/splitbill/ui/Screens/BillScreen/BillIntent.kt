package com.example.splitbill.ui.Screens.BillScreen

sealed class  BillIntent {
    data class LoadBill(val billId: Int) : BillIntent()
    data class AddExpense(val name: String, val amount: Double, val paidById: Long) : BillIntent()
    object CalculateSettlement : BillIntent()
    object DismissSettlementDialog : BillIntent()
}
