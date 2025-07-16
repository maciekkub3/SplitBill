package com.example.splitbill.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["billId", "friendId"])
data class BillParticipant(
    val billId: Long,
    val friendId: Long,
)
