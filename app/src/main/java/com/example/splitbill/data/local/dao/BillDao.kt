package com.example.splitbill.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.splitbill.data.local.entity.Bill
import com.example.splitbill.data.local.entity.BillParticipant
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {

    @Upsert
    suspend fun upsertBill(bill: Bill): Long

    @Delete
    suspend fun deleteBill(bill: Bill)

    @Insert
    suspend fun insertParticipants(participants: List<BillParticipant>)


    @Transaction
    suspend fun insertBillAndParticipants(bill: Bill, participants: List<BillParticipant>): Long {
        val billId = upsertBill(bill)
        val participants = participants.map { BillParticipant(billId = billId, friendId = it.friendId) }
        insertParticipants(participants)
        return billId
    }



    @Query("SELECT * FROM BillParticipant WHERE billId = :billId")
    fun getBillParticipants(billId: Long) : Flow<List<BillParticipant>>

    @Query("SELECT * FROM Bill ORDER BY createdAt DESC")
    fun getAllBills(): Flow<List<Bill>>

    @Query("SELECT * FROM Bill WHERE id = :id LIMIT 1")
    suspend fun getBillById(id: Int): Bill?



}
