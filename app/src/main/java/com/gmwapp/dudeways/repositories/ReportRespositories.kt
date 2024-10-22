package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject

class ReportRespositories @Inject constructor(val apiService: ApiService) {


    suspend fun reportFriends(
        userId: String,
        friendUserId: String,
        message: String
    ) = apiService.reportFriends(
        userId, friendUserId,message
    )

}