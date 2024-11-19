package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject


class CallRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun getRandomUser(
        userId: String
    ) = apiService.getRandomUser(
        userId
    )


    suspend fun setUserCall(
        userId: String,
        callUserId: String,
        startTime: String,
        endTime: String,
        duration: Int
        ) = apiService.setUserCall(
        userId,callUserId,startTime,endTime, duration.toString()
    )


}