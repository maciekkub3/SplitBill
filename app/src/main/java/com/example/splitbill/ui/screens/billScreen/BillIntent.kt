package com.example.splitbill.ui.screens.billScreen

import com.example.splitbill.data.local.entity.Friend

sealed class  BillIntent {
    data class LoadBill(val billId: Long) : BillIntent()
    data class AddExpense(val description: String, val amount: Double, val paidById: Long) : BillIntent()
    object CalculateSettlement : BillIntent()
    object DismissSettlementDialog : BillIntent()
    object ShowAddExpenseDialog : BillIntent()
    object DismissAddExpenseDialog : BillIntent()
    object ShowEditExpenseDialog : BillIntent()
    object DismissEditExpenseDialog : BillIntent()
    data class DescriptionChange(val description: String) : BillIntent()
    data class AmountChange(val amount: String) : BillIntent()
    data class PayerChange(val payer: Friend?) : BillIntent()
    object DeleteBill : BillIntent()
    object ShowDeleteDialog : BillIntent()
    object DismissDeleteDialog : BillIntent()
}
