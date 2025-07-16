package com.example.splitbill.data.repository

import com.example.splitbill.data.local.dao.FriendDao
import com.example.splitbill.data.local.entity.Friend
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FriendRepository @Inject constructor(
    private val friendDao: FriendDao
) {

    val allFriends: Flow<List<Friend>> = friendDao.getFriends()

    suspend fun upsertFriend(friend: Friend) = friendDao.upsertFriend(friend)

    suspend fun deleteFriend(friend: Friend) = friendDao.deleteFriend(friend)

    suspend fun getFriendById(id: Int): Friend? = friendDao.getFriendById(id)
}
