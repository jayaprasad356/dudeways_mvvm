package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject

class FriendRespositories @Inject constructor(val apiService: ApiService) {
    suspend fun addFriends(
        userId: String,
        friendUserId: String,
        friend: String
    ) = apiService.addFriends(
        userId, friendUserId, friend
    )

    suspend fun getConnect(userId: String, offset: String, limit: String) =
        apiService.getConnect(userId, offset, limit)
}