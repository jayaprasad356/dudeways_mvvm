package com.gmwapp.dudeways.network

import android.util.Log
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.HomeProfile
import com.gmwapp.dudeways.model.HomeResponse
import com.gmwapp.dudeways.model.HomeUserResponse
import com.gmwapp.dudeways.model.HomeUserlist
import com.gmwapp.dudeways.model.LoginModel
import com.gmwapp.dudeways.model.LoginResponse
import com.gmwapp.dudeways.model.ProffessionResponse
import com.gmwapp.dudeways.model.RegisterModel
import com.gmwapp.dudeways.model.RegisterResponse
import com.gmwapp.dudeways.model.SettingsResponse
import com.gmwapp.dudeways.model.UserDataResponse
import com.gmwapp.dudeways.utils.Constant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST(Constant.CHECK_EMAIL)
    suspend fun checkEmail(@Field(Constant.EMAIL) email: String): Response<LoginResponse>

    @POST(Constant.PROFESSION_LIST)
    suspend fun getProffession(): Response<ProffessionResponse>

    @FormUrlEncoded
    @POST(Constant.REGISTER)
    suspend fun doRegister(
        @Field(Constant.NAME) name: String,
        @Field(Constant.EMAIL) email: String,
        @Field(Constant.AGE) age: String,
        @Field(Constant.GENDER) gender: String,
        @Field(Constant.PROFESSION_ID) proffessionId: String,
        @Field(Constant.STATE) state: String,
        @Field(Constant.CITY) city: String,
        @Field(Constant.INTRODUCTION) introduction: String,
    ): Response<RegisterResponse>


    @FormUrlEncoded
    @POST(Constant.UPDATE_IMAGE)
    suspend fun updateImage(
        @Field(Constant.USER_ID) userId: String, @Field(Constant.PROFILE) profile: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST(Constant.UPDATE_LOCATION)
    suspend fun updateLocation(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.LATITUDE) latitude: String,
        @Field(Constant.LONGITUDE) longitude: String
    ): Response<BaseResponse>

    @POST(Constant.SETTINGS_LIST)
    suspend fun getSettings(): Response<SettingsResponse>

    @FormUrlEncoded
    @POST(Constant.USERDETAILS)
    suspend fun getUserDetails(
        @Field(Constant.USER_ID) userId: String, @Field(Constant.ONLINE_STATUS) onlineStatus: String
    ): Response<UserDataResponse>


    @FormUrlEncoded
    @POST(Constant.TRIP_LIST)
    suspend fun getTrip(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.TYPE) type: String,
        @Field(Constant.OFFSET) offeset: String,
        @Field(Constant.LIMIT) limit: String,
        @Field("date") date: String
    ): Response<HomeResponse>

    @FormUrlEncoded
    @POST(Constant.ACTIVE_USERS_LIST)
    suspend fun getActiveUser(@Field(Constant.USER_ID) userId: String): Response<HomeUserResponse>

}