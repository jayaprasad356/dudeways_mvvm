package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject


class CallRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun getRandomUser(
        userId: String
    ) = apiService.getRandomUser(
        userId
    )


}