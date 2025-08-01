package com.example.splitbill.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.splitbill.data.local.entity.BillParticipant
import kotlinx.coroutines.flow.Flow

@Dao
interface BillParticipantDao {

    @Upsert
    suspend fun upsertBillParticipants(billParticipants: List<BillParticipant>)

    @Delete
    suspend fun deleteBillParticipant(billParticipant: BillParticipant)

    @Query("SELECT * FROM BillParticipant WHERE billId = :billId")
    suspend fun getParticipantsForBill(billId: Long): List<BillParticipant>

    @Query("SELECT * FROM BillParticipant")
    fun getAllParticipants(): Flow<List<BillParticipant>>

}
