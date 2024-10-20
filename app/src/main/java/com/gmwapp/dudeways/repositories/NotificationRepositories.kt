package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject


class NotificationRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun getNotifications(
        userId: String,
        offset: String, limit: String
    ) = apiService.getNotifications(
        userId, offset, limit
    )
}