package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.UserEarningsResponse
import com.gmwapp.dudeways.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject


class EarningRepositories @Inject constructor(val apiService: ApiService) {


    suspend fun updateMobile(
        userId: String, mobile: String,
    ) = apiService.updateMobile(
        userId, mobile
    )


//    suspend fun updateImage(
//        userId: RequestBody,
//        selfi_image: MultipartBody.Part?
//    ) = apiService.updateSelfiImage(
//        userId,
//        selfi_image
//    )

    suspend fun updateImage(
        userId: RequestBody,
        type: RequestBody,
        selfi_image: MultipartBody.Part?,
        proof_image: MultipartBody.Part?
    ): Response<UserEarningsResponse> {  // Ensure the correct type here
        return apiService.updateEarningImage(userId, type, selfi_image, proof_image)
    }


    suspend fun updateWithOutVerification(
        userId: String, type: String
    ) = apiService.updateWithoutProofImage(userId, type)


}