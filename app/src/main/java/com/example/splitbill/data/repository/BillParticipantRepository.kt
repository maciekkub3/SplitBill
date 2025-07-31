package com.example.splitbill.data.repository


import com.example.splitbill.data.local.dao.BillParticipantDao
import com.example.splitbill.data.local.entity.BillParticipant
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BillParticipantRepository @Inject constructor(
    private val billParticipantDao: BillParticipantDao
) {
    suspend fun getParticipantsForBill(billId: Long): List<BillParticipant> = billParticipantDao.getParticipantsForBill(billId)

    suspend fun upsertBillParticipants(participants: List<BillParticipant>) = billParticipantDao.upsertBillParticipants(participants)

    suspend fun deleteBillParticipant(participant: BillParticipant) = billParticipantDao.deleteBillParticipant(participant)

    fun getAllParticipants(): Flow<List<BillParticipant>> = billParticipantDao.getAllParticipants()
}
