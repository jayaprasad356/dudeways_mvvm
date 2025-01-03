package com.gmwapp.dudeways.network

import android.util.Log
import com.gmwapp.dudeways.model.AddChatResponse
import com.gmwapp.dudeways.model.AddPointsResponse
import com.gmwapp.dudeways.model.AddTripResponse
import com.gmwapp.dudeways.model.AppUpdateResponse
import com.gmwapp.dudeways.model.BackImageResponse
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.CallResponse
import com.gmwapp.dudeways.model.ChatResponse
import com.gmwapp.dudeways.model.ConnectModel
import com.gmwapp.dudeways.model.ConnectResponse
import com.gmwapp.dudeways.model.CoverImageResponse
import com.gmwapp.dudeways.model.FontImageResponse
import com.gmwapp.dudeways.model.HomeProfile
import com.gmwapp.dudeways.model.HomeResponse
import com.gmwapp.dudeways.model.HomeUserResponse
import com.gmwapp.dudeways.model.HomeUserlist
import com.gmwapp.dudeways.model.LoginModel
import com.gmwapp.dudeways.model.LoginResponse
import com.gmwapp.dudeways.model.MyTripResponse
import com.gmwapp.dudeways.model.NotificationResponse
import com.gmwapp.dudeways.model.OtherUserDetailModel
import com.gmwapp.dudeways.model.OtherUserDetailResponse
import com.gmwapp.dudeways.model.PlanListResponse
import com.gmwapp.dudeways.model.PrivarcyPolicyResponse
import com.gmwapp.dudeways.model.ProffessionResponse
import com.gmwapp.dudeways.model.PurchaseResponse
import com.gmwapp.dudeways.model.RefundPolicyResponse
import com.gmwapp.dudeways.model.RegisterModel
import com.gmwapp.dudeways.model.RegisterResponse
import com.gmwapp.dudeways.model.RewardResponse
import com.gmwapp.dudeways.model.SearchResponse
import com.gmwapp.dudeways.model.SelfiImageResponse
import com.gmwapp.dudeways.model.SettingsResponse
import com.gmwapp.dudeways.model.TermsConditionResponse
import com.gmwapp.dudeways.model.TransactionResponse
import com.gmwapp.dudeways.model.UserDataResponse
import com.gmwapp.dudeways.model.UserEarningsResponse
import com.gmwapp.dudeways.model.VoiceUpdateResponse
import com.gmwapp.dudeways.model.WalletResponse
import com.gmwapp.dudeways.model.WithdrawResponse
import com.gmwapp.dudeways.utils.Constant
import com.google.android.gms.fido.u2f.api.messagebased.ResponseType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @POST(Constant.APPUPDATE)
    suspend fun appUpdate():Response<AppUpdateResponse>

    @FormUrlEncoded
    @POST(Constant.CHECK_EMAIL)
    suspend fun checkEmail(@Field(Constant.EMAIL) email: String): Response<LoginResponse>

    @FormUrlEncoded
    @POST(Constant.CHECK_MOBILE)
    suspend fun checkMobile(@Field(Constant.MOBILE) mobile: String): Response<LoginResponse>





    @POST(Constant.PROFESSION_LIST)
    suspend fun getProffession(): Response<ProffessionResponse>

    @FormUrlEncoded
    @POST(Constant.REGISTER)
    suspend fun doRegister(
        @Field(Constant.MOBILE) mobile: String,
        @Field(Constant.DOB) dob: String,
        @Field(Constant.NAME) name: String,
        @Field(Constant.EMAIL) email: String,
        @Field(Constant.AGE) age: String,
        @Field(Constant.GENDER) gender: String,
        @Field(Constant.PROFESSION_ID) proffessionId: String,
        @Field(Constant.STATE) state: String,
        @Field(Constant.CITY) city: String,
        @Field(Constant.INTRODUCTION) introduction: String,
        @Field(Constant.LANGUAGE) language: String
    ): Response<RegisterResponse>


    @FormUrlEncoded
    @POST(Constant.UPDATE_PROFILE)
    suspend fun doUpdateProfile(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.NAME) name: String,
    ): Response<BaseResponse>


    @FormUrlEncoded
    @POST(Constant.NEW_REGISTER)
    suspend fun doRegisternew(
        @Field(Constant.MOBILE) mobile: String,
        @Field(Constant.NAME) name: String,
        @Field(Constant.DOB) dob: String,
        @Field(Constant.GENDER) gender: String,
        @Field(Constant.LANGUAGE) language: String
    ): Response<RegisterResponse>


    @Multipart
    @POST(Constant.UPDATE_IMAGE)
    suspend fun updateImage(
        @Part(Constant.USER_ID) userId: RequestBody,
        @Part profile: MultipartBody.Part?
    ): Response<RegisterResponse>


    @Multipart
    @POST(Constant.VOICE_VERIFICATION)
    suspend fun voiceVerification(
        @Part(Constant.USER_ID) userId: RequestBody,
        @Part voice: MultipartBody.Part // This is where we send the audio file
    ): Response<VoiceUpdateResponse>



    @Multipart
    @POST(Constant.USER_EARNINGS)
    suspend fun updateEarningImage(
        @Part(Constant.USER_ID) userId: RequestBody,
        @Part(Constant.TYPE) type: RequestBody,
        @Part selfi_image: MultipartBody.Part?,
        @Part proof_image: MultipartBody.Part?,
        ): Response<UserEarningsResponse>

    @FormUrlEncoded
    @POST(Constant.USER_EARNINGS_WITHOUT_PROOF)
    suspend fun updateWithoutProofImage(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.TYPE) type: String,
        ): Response<BaseResponse>


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
    @POST(Constant.ADD_REPORTS)
    suspend fun reportFriends(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.CHAT_USER_ID) friendUserId: String,
        @Field(Constant.MESSAGE) message: String
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
        @Part(Constant.TRIP_ID) uid: RequestBody, @Part image: MultipartBody.Part?
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
    suspend fun getPurchase(@Field(Constant.USER_ID) userId: String): Response<PurchaseResponse>

    @FormUrlEncoded
    @POST(Constant.ADD_POINTS_REQUEST)
    suspend fun addPoints(
        @Field("buyer_name") buyer_name: String,
        @Field("amount") amount: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("purpose") purpose: String,
    ): Response<AddPointsResponse>

    @FormUrlEncoded
    @POST(Constant.REWARD_POINTS)
    suspend fun addPurchase(
        @Field(Constant.USER_ID) userId: String, @Field("points") points: String
    ): Response<RewardResponse>


    @FormUrlEncoded
    @POST(Constant.SPIN_POINTS)
    suspend fun spinPoints(
        @Field(Constant.USER_ID) userId: String, @Field("points") points: String
    ): Response<RewardResponse>

    @FormUrlEncoded
    @POST(Constant.OTHER_USER_DETAILS)
    suspend fun otherUserDetails(
        @Field(Constant.USER_ID) userId: String, @Field(Constant.OTHER_USER_ID) otherUserId: String
    ): Response<OtherUserDetailResponse>

    @FormUrlEncoded
    @POST(Constant.PLAN_LIST)
    suspend fun getPlan(@Field(Constant.USER_ID) userId: String): Response<PlanListResponse>

    @Multipart
    @POST(Constant.PAYMENT_IMAGE_API)
    suspend fun getPaymentImage(
        @Part(Constant.USER_ID) uid: RequestBody, @Part image: MultipartBody.Part?
    ): Response<BaseResponse>

    @Multipart
    @POST(Constant.VERIFY_SELFIE_IMAGE)
    suspend fun verifySelfiImage(
        @Part(Constant.USER_ID) uid: RequestBody, @Part image: MultipartBody.Part?
    ): Response<SelfiImageResponse>

    @Multipart
    @POST(Constant.VERIFY_FRONT_IMAGE)
    suspend fun verifyFrontImage(
        @Part(Constant.USER_ID) uid: RequestBody, @Part image: MultipartBody.Part?
    ): Response<FontImageResponse>

    @Multipart
    @POST(Constant.VERIFY_BACK_IMAGE)
    suspend fun verifyBackImage(
        @Part(Constant.USER_ID) uid: RequestBody, @Part image: MultipartBody.Part?
    ): Response<BackImageResponse>


    @FormUrlEncoded
    @POST(Constant.DELETE_CHAT)
    suspend fun deleteChat(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.CHAT_USER_ID) chatUserId: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(Constant.ADD_CHAT)
    fun addChat(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.CHAT_USER_ID) chatUserId: String,
        @Field(Constant.UNREAD) unread: String,
        @Field(Constant.MSG_SEEN) msgSeen: String,
        @Field(Constant.MESSAGE) message: String
    ): Call<AddChatResponse>

    @FormUrlEncoded
    @POST(Constant.NOTFICATION_LIST)
    suspend fun getNotifications(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.OFFSET) offset: String,
        @Field(Constant.LIMIT) limit: String
    ): Response<NotificationResponse>

    @FormUrlEncoded
    @POST(Constant.USER_TRANSACTION_LIST)
    suspend fun getTransaction(
        @Field(Constant.USER_ID) userId: String,
    ): Response<TransactionResponse>

    @FormUrlEncoded
    @POST(Constant.RANDOM_USER)
    suspend fun getRandomUser(
        @Field(Constant.USER_ID) userId: String
    ): Response<CallResponse>

    @FormUrlEncoded
    @POST(Constant.USER_CALL)
    suspend fun setUserCall(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.CALL_USER_ID) callUserId: String,
        @Field(Constant.START_TIME) startTime: String,
        @Field(Constant.END_TIME) endTime: String,
        @Field(Constant.DURATION) duration: String
    ): Response<BaseResponse>




    @FormUrlEncoded
    @POST(Constant.USERS_LIST)
    suspend fun getUsers(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.OFFSET) offset: String,
        @Field(Constant.LIMIT) limit: String,
        @Field(Constant.GENDER) gender: String
    ): Response<SearchResponse>

    @FormUrlEncoded
    @POST(Constant.DELETE_ACCOUNT)
    suspend fun deleteAccount(
        @Field(Constant.USER_ID) userId: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(Constant.ADD_FEEDBACK)
    suspend fun addFeedBack(
        @Field(Constant.USER_ID) userId: String,
        @Field("feedback") feedBack: String
    ): Response<BaseResponse>

    @POST(Constant.TERMS_CONDITIONS)
    suspend fun termsConditions(): Response<TermsConditionResponse>

    @POST(Constant.REFUND_POLICY)
    suspend fun getRefundPolicy(): Response<RefundPolicyResponse>

    @POST(Constant.PRIVACY_POLICY)
    suspend fun getPrivarcyPolicy(): Response<PrivarcyPolicyResponse>

    @Multipart
    @POST(Constant.UPDATE_COVER_IMG)
    suspend fun getCoverImage(
        @Part(Constant.USER_ID) userId: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<CoverImageResponse>

    @FormUrlEncoded
    @POST(Constant.UPDATE_NOTIFY)
    suspend fun updateNotify(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.MESSAGE_NOTIFY) notify: String,
        @Field(Constant.ADD_FRIEND_NOTIFY) addFriendNotify: String,
        @Field(Constant.VIEW_NOTIFY) viewNotify: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(Constant.UPDATE_MOBILE)
    suspend fun updateMobile(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.MOBILE) notify: String,
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST(Constant.WITHDRAWALS_LIST)
    suspend fun getWithdraw(
        @Field(Constant.USER_ID) userId: String
    ): Response<WithdrawResponse>

    @FormUrlEncoded
    @POST(Constant.WITHDRAWALS)
    suspend fun withdraw(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.AMOUNT) amount: String
    ): Response<WalletResponse>

    @FormUrlEncoded
    @POST(Constant.UPDATE_BANK)
    suspend fun updateBank(
        @Field(Constant.USER_ID) userId: String,
        @Field(Constant.ACCOUNT_HOLDER_NAME) name: String,
        @Field(Constant.ACCOUNT_NUMBER) number: String,
        @Field(Constant.IFSC_CODE) ifsc: String,
        @Field(Constant.BANK_NAME) bankName: String,
        @Field(Constant.BRANCH_NAME) branchName: String
    ): Response<BaseResponse>





    @FormUrlEncoded
    @POST(Constant.TRIP_LIST)
    suspend fun getSticker(
        @Field(Constant.USER_ID) userId: String,
    ): Response<HomeResponse>



}