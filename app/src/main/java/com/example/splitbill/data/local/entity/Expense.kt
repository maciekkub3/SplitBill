package com.example.splitbill.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Bill::class,
            parentColumns = ["id"],
            childColumns = ["billId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Friend::class,
            parentColumns = ["id"],
            childColumns = ["paidById"],
            onDelete = ForeignKey.SET_NULL
        ),
    ],
    indices = [Index("billId"), Index("paidById")]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val billId: Long,
    val name: String,
    val amount: Double,
    val paidById: Long?,
    val date: Long = System.currentTimeMillis()
)
