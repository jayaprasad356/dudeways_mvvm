package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject

class ChatRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun getChat(userId: String, offset: String, limit: String) = apiService.getChat(
        userId, offset, limit
    )

    suspend fun getUnReadMessage(userId: String) = apiService.getUnreadMessage(
        userId
    )

    suspend fun getReadChats(
        userId: String, chatUserId: String, msgSeen: String,
    ) = apiService.getReadChats(userId, chatUserId, msgSeen)

    suspend fun getPurchase(userId: String) = apiService.getPurchase(userId)

    suspend fun addPoints(
        buyerName: String,
        amount: String, email: String, phone: String,
        purpose: String
    ) = apiService.addPoints(
        buyerName, amount, email, phone, purpose
    )

    suspend fun addPurchase(
        userId: String,
        points: String
    ) = apiService.addPurchase(
        userId, points
    )

    suspend fun otherUserDetails(
        userId: String,
        otherUserId: String
    ) = apiService.otherUserDetails(
        userId, otherUserId
    )

}