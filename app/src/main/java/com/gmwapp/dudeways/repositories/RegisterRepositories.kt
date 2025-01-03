package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class RegisterRepositories @Inject constructor(val apiService: ApiService) {


    suspend fun doRegister(
        mobile : String,
        dob : String,
        name: String, email: String,
        age: String, gender: String,
        proffessionId: String,
        state: String, city: String,
        introduction: String,
        language: String,
    ) = apiService.doRegister(
        mobile,dob,name, email, age, gender, proffessionId, state, city, introduction,language
    )


    suspend fun doUpdateProfile(
        user_id : String,
        name: String,

    ) = apiService.doUpdateProfile(
       user_id,name
    )



    suspend fun doRegisternew(
        mobile : String,
        name: String,
        dob: String,
        gender: String,
        language: String,
    ) = apiService.doRegisternew(
       mobile, name,dob, gender,language
    )

    suspend fun updateImage(userId:RequestBody,
                            filePath:MultipartBody.Part?)=apiService.updateImage(userId,
                                filePath)
}