package com.example.splitbill.data.repository

import com.example.splitbill.data.local.dao.BillDao
import com.example.splitbill.data.local.entity.Bill
import com.example.splitbill.data.local.entity.BillParticipant
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BillRepository @Inject constructor(
    private val billDao: BillDao
) {
    suspend fun allBills(): Flow<List<Bill>> = billDao.getAllBills()

    suspend fun upsertBill(bill: Bill) = billDao.upsertBill(bill)

    suspend fun deleteBill(bill: Bill) = billDao.deleteBill(bill)

    suspend fun getById(id: Long): Bill? = billDao.getBillById(id)

    suspend fun insertParticipants(participants: List<BillParticipant>) = billDao.insertParticipants(participants)

    suspend fun insertBillAndParticipants(bill: Bill, participants: List<Long>) {
        val billId = upsertBill(bill)
        val billParticipants = participants.map { BillParticipant(billId, it) }
        insertParticipants(billParticipants)
    }
}
