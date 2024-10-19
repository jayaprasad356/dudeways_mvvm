package com.gmwapp.dudeways.network

import android.util.Log
import com.gmwapp.dudeways.model.AddTripResponse
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.ChatResponse
import com.gmwapp.dudeways.model.ConnectModel
import com.gmwapp.dudeways.model.ConnectResponse
import com.gmwapp.dudeways.model.HomeProfile
import com.gmwapp.dudeways.model.HomeResponse
import com.gmwapp.dudeways.model.HomeUserResponse
import com.gmwapp.dudeways.model.HomeUserlist
import com.gmwapp.dudeways.model.LoginModel
import com.gmwapp.dudeways.model.LoginResponse
import com.gmwapp.dudeways.model.MyTripResponse
import com.gmwapp.dudeways.model.OtherUserDetailModel
import com.gmwapp.dudeways.model.OtherUserDetailResponse
import com.gmwapp.dudeways.model.ProffessionResponse
import com.gmwapp.dudeways.model.PurchaseResponse
import com.gmwapp.dudeways.model.RegisterModel
import com.gmwapp.dudeways.model.RegisterResponse
import com.gmwapp.dudeways.model.RewardResponse
import com.gmwapp.dudeways.model.SettingsResponse
import com.gmwapp.dudeways.model.UserDataResponse
import com.gmwapp.dudeways.utils.Constant
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    @FormUrlEncoded
    @POST(Constant.ADD_FRIENDS)
    suspend fun addFriends(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.FRIEND_USER_ID) friendUserId: String,
        @Field(Constant.FRIEND) friend: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(Constant.FREINDS_LIST)
    suspend fun getConnect(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.OFFSET) offeset: String,
        @Field(Constant.LIMIT) limit: String
    ): Response<ConnectResponse>

    @Multipart
    @POST(Constant.UPDATE_TRIP_IMAGE)
    suspend fun updateTripImage(
        @Part(Constant.TRIP_ID) uid: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(Constant.ADD_TRIP)
    suspend fun addTrip(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.TRIP_TYPE) tripType: String,
        @Field(Constant.TRIP_FROM_DATE) fromDate: String,
        @Field(Constant.TRIP_TO_DATE) toDate: String,
        @Field(Constant.TRIP_TITLE) title: String,
        @Field(Constant.TRIP_DESCRIPTION) description: String,
        @Field(Constant.TRIP_LOCATION) location: String,
        @Field(Constant.PROFILE_IMAGE) profileImage: String
    ): Response<AddTripResponse>

    @FormUrlEncoded
    @POST(Constant.MY_TRIP_LIST)
    suspend fun myTrip(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.OFFSET) offset: String,
        @Field(Constant.LIMIT) limit: String
    ): Response<MyTripResponse>

    @FormUrlEncoded
    @POST(Constant.DELETE_TRIP)
    suspend fun deleteTrip(
        @Field("trip_id") trip_id: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(Constant.CHAT_LIST)
    suspend fun getChat(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.OFFSET) offset: String,
        @Field(Constant.LIMIT) limit: String
    ): Response<ChatResponse>

    @FormUrlEncoded
    @POST(Constant.UNREAD_ALL)
    suspend fun getUnreadMessage(
        @Field(Constant.USER_ID) userId: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(Constant.READ_CHATS)
    suspend fun getReadChats(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.CHAT_USER_ID) chatUserId: String,
        @Field(Constant.MSG_SEEN) msgSeen: String
    ): Response<BaseResponse>


    @FormUrlEncoded
    @POST(Constant.POINTS_LIST)
    suspend fun getPurchase(@Field(Constant.USER_ID) userId: String):
            Response<PurchaseResponse>

    @FormUrlEncoded
    @POST(Constant.ADD_POINTS_REQUEST)
    suspend fun addPoints(
        @Field("buyer_name") buyer_name: String,
        @Field("amount") amount: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("purpose") purpose: String,
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(Constant.REWARD_POINTS)
    suspend fun addPurchase(
        @Field(Constant.USER_ID) userId: String,
        @Field("points") points: String
    ): Response<RewardResponse>

    @FormUrlEncoded
    @POST(Constant.OTHER_USER_DETAILS)
    suspend fun otherUserDetails(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.OTHER_USER_ID) otherUserId: String
    ): Response<OtherUserDetailResponse>
}