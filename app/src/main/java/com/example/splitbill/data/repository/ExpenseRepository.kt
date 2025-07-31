package com.example.splitbill.data.repository

import com.example.splitbill.data.classes.BillExpenseSum
import com.example.splitbill.data.local.dao.ExpenseDao
import com.example.splitbill.data.local.entity.Expense
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {

    suspend fun getExpensesForBill(billId: Long): List<Expense> = expenseDao.getExpensesForBill(billId)

    suspend fun upsertExpense(expense: Expense) = expenseDao.upsertExpense(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)

    suspend fun getTotalAmountPerBill(): List<BillExpenseSum> = expenseDao.getTotalAmountPerBill()
}

