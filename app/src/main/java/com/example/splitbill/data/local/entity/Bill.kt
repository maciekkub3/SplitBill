package com.example.splitbill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)
