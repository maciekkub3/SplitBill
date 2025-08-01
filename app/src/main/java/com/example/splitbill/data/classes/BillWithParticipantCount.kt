package com.example.splitbill.data.classes

data class BillWithParticipantCount(
    val id: Long,
    val title: String,
    val createdAt: Long,
    val participantCount: Int,
    val totalAmount: Double? = null,
)
