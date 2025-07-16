package com.example.splitbill.data.repository

import com.example.splitbill.data.local.dao.BillDao
import com.example.splitbill.data.local.entity.Bill
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BillRepository @Inject constructor(
    private val billDao: BillDao
) {
    val allBills: Flow<List<Bill>> = billDao.getAllBills()

    suspend fun upsertBill(bill: Bill) = billDao.upsertBill(bill)

    suspend fun deleteBill(bill: Bill) = billDao.deleteBill(bill)

    suspend fun getById(id: Int): Bill? = billDao.getBillById(id)
}
