package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject

class RegisterRepositories @Inject constructor(val apiService: ApiService) {
    suspend fun doRegister(
        name: String, email: String,
        age: String, gender: String,
        proffessionId: String,
        state: String, city: String,
        introduction: String
    ) = apiService.doRegister(
        name, email, age, gender, proffessionId, state, city, introduction
    )

    suspend fun updateImage(userId:String,
                            filePath:String)=apiService.updateImage(userId,
                                filePath)
}