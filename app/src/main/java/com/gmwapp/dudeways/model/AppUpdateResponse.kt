package com.gmwapp.dudeways.model

data class AppUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<AppUpdateModel>
)

data class AppUpdateModel(
    val app_version: String,
    val link: String,
    val login: String,
    val description: String
)