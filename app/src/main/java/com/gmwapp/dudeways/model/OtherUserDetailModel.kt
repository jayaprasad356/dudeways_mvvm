package com.gmwapp.dudeways.model


data class OtherUserDetailResponse(
    val success: Boolean,
    val message: String,
    val data: OtherUserDetailModel

)


data class OtherUserDetailModel(
    val verified: String,
    val unique_name: String,
    val name: String,
    val profile: String
)