package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject

class SettingsRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun getSettings() = apiService.getSettings()
}