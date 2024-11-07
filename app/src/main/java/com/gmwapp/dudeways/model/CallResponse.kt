package com.gmwapp.dudeways.model

data class CallResponse(
    val success: Boolean,
    val message: String,
    val data: UserData
)

data class UserData(
    var id: Int? = 0,
    val name: String,
    val unique_name: String,
    val email: String,
    val mobile: String,
    val age: Int,
    val gender: String,
    val state: String,
    val city: String,
    val profession: String,
    val refer_code: String,
    val referred_by: String?,
    val profile: String?,
    val cover_img: String?,
    val points: Int,
    val verified: Int,
    val online_status: Int,
    val introduction: String?,
    val message_notify: Int,
    val add_friend_notify: Int,
    val view_notify: Int,
    val profile_verified: Int,
    val cover_img_verified: Int,
    val latitude: String?,
    val longtitude: String?,
    val balance: String,
    val selfi_image: String?,
    val proof_image: String?,
    val datetime: String,
    val updated_at: String,
    val created_at: String
)
