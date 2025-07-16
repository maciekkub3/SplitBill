package com.example.splitbill.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.splitbill.data.local.entity.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Upsert
    suspend fun upsertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM Expense WHERE billId = :billId ORDER BY date DESC")
    fun getExpensesForBill(billId: Long): Flow<List<Expense>>

    @Query("SELECT * FROM Expense WHERE id = :id LIMIT 1")
    suspend fun getExpenseById(id: Long): Expense?
}
