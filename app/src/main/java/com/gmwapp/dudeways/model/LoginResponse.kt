package com.gmwapp.dudeways.model

data class LoginResponse(
    val success: Boolean,
    val registered: Boolean,
    val message: String,
    val data:LoginModel
)

data class LoginModel(
    val id: String,
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
    val dob: String,
    val verified: Int,
    val online_status: Int,
    val introduction: String,
    val message_notify: Int,
    val add_friend_notify: Int,
    val view_notify: Int,
    val profile_verified: Int,
    val cover_img_verified: Int,
    val voice_verification_status: Int,

)