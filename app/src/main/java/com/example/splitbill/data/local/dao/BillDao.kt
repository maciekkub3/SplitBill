package com.example.splitbill.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.splitbill.data.local.entity.Bill
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {

    @Upsert
    suspend fun upsertBill(bill: Bill)

    @Delete
    suspend fun deleteBill(bill: Bill)

    @Query("SELECT * FROM Bill ORDER BY createdAt DESC")
    fun getAllBills(): Flow<List<Bill>>

    @Query("SELECT * FROM Bill WHERE id = :id LIMIT 1")
    suspend fun getBillById(id: Int): Bill?

}
