package com.gmwapp.dudeways.fragment

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthKeyApi {
    @FormUrlEncoded
    @POST("request?")
    fun sendOTP(
        @Query("authkey") authKey: String,
        @Query("mobile") mobile: String,
        @Query("country_code") countryCode: String,
        @Query("sid") sid: String,
        @Query("otp") otp: String,
        @Field("test") test: String,
    ): Call<Void>
}
