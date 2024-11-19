package com.gmwapp.dudeways.model

data class SettingsResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<SettingsModel>
)

data class SettingsModel(
    val instagram_link: String,
    val telegram_link: String,
    val upi_id: String,
    val mobile: String,
    val verification_cost: String,
    val without_verification_cost: String
)