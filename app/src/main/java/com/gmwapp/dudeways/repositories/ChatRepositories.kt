package com.gmwapp.dudeways.repositories

import android.util.Log
import com.gmwapp.dudeways.model.AddChatResponse
import com.gmwapp.dudeways.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    suspend fun spinPoint(
        userId: String,
        points: String
    ) = apiService.spinPoints(
        userId, points
    )

    suspend fun otherUserDetails(
        userId: String,
        otherUserId: String
    ) = apiService.otherUserDetails(
        userId, otherUserId
    )

    suspend fun getPlan(userId: String) = apiService.getPlan(userId)

    suspend fun getPaymentImage(
        uid: RequestBody,
        image: MultipartBody.Part?
    ) = apiService.getPaymentImage(
        uid, image
    )

    suspend fun selfiImageResponse(
        uid: RequestBody,
        image: MultipartBody.Part?
    ) = apiService.verifySelfiImage(
        uid, image
    )

    suspend fun verifyFontImage(
        uid: RequestBody,
        image: MultipartBody.Part?
    ) = apiService.verifyFrontImage(
        uid, image
    )

    suspend fun verifyBackImage(
        uid: RequestBody,
        image: MultipartBody.Part?
    ) = apiService.verifyBackImage(
        uid, image
    )

    suspend fun deleteChat(userId: String,chatUserId: String)=apiService.deleteChat(
        userId,chatUserId
    )

    fun addChat(userId: String,chatUserId: String,
                        unRead:String,msgSeen: String,message:String, callback: Callback<AddChatResponse>) {
        Log.e("siva", "api called")
        val chats: Call<AddChatResponse> =
            apiService.addChat(userId, chatUserId, unRead, msgSeen, message)
        return chats.enqueue(callback)
    }

}