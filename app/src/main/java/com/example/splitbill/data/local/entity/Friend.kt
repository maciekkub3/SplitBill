package com.example.splitbill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Friend(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val avatarUri: String? = null
)
