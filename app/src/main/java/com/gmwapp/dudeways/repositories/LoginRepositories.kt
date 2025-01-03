package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.model.VoiceUpdateResponse
import com.gmwapp.dudeways.network.ApiService
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class LoginRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun appUpdate() = apiService.appUpdate()
    suspend fun checkEmail(email: String) = apiService.checkEmail(email)
    suspend fun checkMobile(mobile: String) = apiService.checkMobile(mobile)
    suspend fun getProffession() = apiService.getProffession()
    suspend fun addVoice(userId: RequestBody, audioFilePath: String): Response<VoiceUpdateResponse> {
        val file = File(audioFilePath)
        val requestFile = RequestBody.create("audio/mp3".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("voice", file.name, requestFile)

        return apiService.voiceVerification(userId, body)
    }
}