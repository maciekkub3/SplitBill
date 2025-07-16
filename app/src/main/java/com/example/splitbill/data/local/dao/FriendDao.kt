package com.example.splitbill.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.splitbill.data.local.entity.Friend
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {

    @Upsert
    suspend fun upsertFriend(friend: Friend)

    @Delete
    suspend fun deleteFriend(friend: Friend)

    @Query("SELECT * FROM friend ORDER BY name ASC")
    fun getFriends(): Flow<List<Friend>>

    @Query("SELECT * FROM Friend WHERE id = :id LIMIT 1")
    suspend fun getFriendById(id: Int): Friend?

}
