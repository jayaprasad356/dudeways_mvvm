package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject


class TransactionRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun getTransaction(
        userId: String
    ) = apiService.getTransaction(
        userId
    )


}