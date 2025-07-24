package com.example.splitbill.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["billId", "friendId"],
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
            childColumns = ["friendId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("billId"), Index("friendId")]
)
data class BillParticipant(
    val billId: Long,
    val friendId: Long,
)