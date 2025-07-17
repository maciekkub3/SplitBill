package com.example.splitbill.ui.Screens.BillScreen

import com.example.splitbill.data.local.entity.Bill

data class BillUiState(
    val bill: Bill? = null,
    val expenses: List<ExpenseDisplayItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val settlementResult: List<SettlementEntry> = emptyList(),
    val showSettlementDialog: Boolean = false

)

data class ExpenseDisplayItem(
    val id: Long,
    val name: String,
    val amount: Double,
    val paidByName: String?, // null if deleted
    val date: Long
)

data class SettlementEntry(
    val from: String,
    val to: String,
    val amount: Double
)
