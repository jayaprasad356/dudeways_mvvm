package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject


class EarningRepositories @Inject constructor(val apiService: ApiService) {


    suspend fun updateMobile(
        userId: String, mobile: String,
        ) = apiService.updateMobile(
        userId, mobile
    )


}