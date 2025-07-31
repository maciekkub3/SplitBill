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

    @Query("SELECT * FROM Friend ORDER BY name ASC")
    fun getFriends(): Flow<List<Friend>>

    @Query("SELECT * FROM Friend WHERE id = :id LIMIT 1")
    suspend fun getFriendById(id: Long): Friend?

    @Query("SELECT * FROM Friend WHERE id IN (:ids)")
    suspend fun getFriendsByIds(ids: List<Long>): List<Friend>

    @Query("UPDATE Friend SET name = :newName WHERE id = :id")
    suspend fun updateFriendName(id: Long?, newName: String)


}
