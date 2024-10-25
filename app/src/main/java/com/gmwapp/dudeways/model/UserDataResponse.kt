package com.gmwapp.dudeways.model

data class UserDataResponse(
    val success: Boolean,
    val message: String,
    val data: UserDataModel
)

data class UserDataModel(
    val name: String,
    val unique_name: String,
    val email: String,
    val mobile: String,
    val age: String,
    val gender: String,
    val state: String,
    val city: String,
    val profession: String,
    val refer_code: String,
    val referred_by: String,
    val profile: String,
    val cover_img: String,
    val points: Int,
    val verified: Int,
    val online_status: Int,
    val introduction: String,
    val message_notify: Int,
    val add_friend_notify: Int,
    val view_notify: Int,
    val profile_verified: Int,
    val cover_img_verified: Int,
    val unread_count: String,
    val balance: String,
    val latitude: String,
    val longtitude: String,
    val selfi_image: String,
    val proof_image: String,

)