package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ProfileRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun deleteAccount(userId: String) = apiService.deleteAccount(
        userId.toString()
    )

    suspend fun addFeedBack(userId: String, feedBack: String) =
        apiService.addFeedBack(userId, feedBack)

    suspend fun getTermsCondiitons() = apiService.termsConditions()

    suspend fun getRefundPolciy() = apiService.getRefundPolicy()

    suspend fun getPrivarcyPolicy() = apiService.getPrivarcyPolicy()

    suspend fun getCoverImage(uid:RequestBody,
                              image:MultipartBody.Part?)=apiService.getCoverImage(
                                  userId = uid,
                                  image
                              )


}