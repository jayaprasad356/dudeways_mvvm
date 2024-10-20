package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject

class SearchRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun getUsers(
        userId: String,
        offset: String,
        limit: String,
        gender: String
    ) = apiService.getUsers(
        userId, offset, limit, gender
    )
}