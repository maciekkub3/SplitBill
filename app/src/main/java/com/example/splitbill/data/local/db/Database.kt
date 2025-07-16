package com.example.splitbill.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.splitbill.data.local.dao.BillDao
import com.example.splitbill.data.local.dao.BillParticipantDao
import com.example.splitbill.data.local.dao.ExpenseDao
import com.example.splitbill.data.local.dao.FriendDao
import com.example.splitbill.data.local.entity.Bill
import com.example.splitbill.data.local.entity.BillParticipant
import com.example.splitbill.data.local.entity.Expense
import com.example.splitbill.data.local.entity.Friend

@Database(
    entities = [
        Friend::class,
        Bill::class,
        BillParticipant::class,
        Expense::class,
    ],
    version = 1
)
abstract class Database: RoomDatabase() {

    abstract val FriendDao: FriendDao
    abstract val BillDao: BillDao
    abstract val ExpenseDao: ExpenseDao
    abstract val BillParticipantDao: BillParticipantDao

}
