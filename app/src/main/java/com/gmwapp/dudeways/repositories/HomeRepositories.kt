package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject

class HomeRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun updateLocation(
        userId: String, latitude: String,
        longitude: String
    ) = apiService.updateLocation(userId, latitude, longitude)

    suspend fun getUserDetails(userId: String, onlineStatus: String) =
        apiService.getUserDetails(userId, onlineStatus)

    suspend fun getTrip(userId: String, type: String, offset: String, limit: String, date: String) =
        apiService.getTrip(
            userId, type, offset, limit, date
        )

    suspend fun getActiveUsers(userId: String) = apiService.getActiveUser(userId)
}