package com.example.splitbill.data.repository

import com.example.splitbill.data.local.dao.FriendDao
import com.example.splitbill.data.local.entity.Friend
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FriendRepository @Inject constructor(
    private val friendDao: FriendDao
) {
    fun getFriends(): Flow<List<Friend>> = friendDao.getFriends()

    suspend fun upsertFriend(friend: Friend) = friendDao.upsertFriend(friend)

    suspend fun deleteFriend(friend: Friend) = friendDao.deleteFriend(friend)

    suspend fun getFriendById(id: Long): Friend? = friendDao.getFriendById(id)

    suspend fun updateFriendName(id: Long?, newName: String) = friendDao.updateFriendName(id, newName)

    suspend fun getFriendsByIds(ids: List<Long>): List<Friend> = friendDao.getFriendsByIds(ids)
}
