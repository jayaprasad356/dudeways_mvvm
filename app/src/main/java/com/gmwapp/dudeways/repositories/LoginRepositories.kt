package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject

class LoginRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun checkEmail(email: String) = apiService.checkEmail(email)
    suspend fun getProffession() = apiService.getProffession()
}