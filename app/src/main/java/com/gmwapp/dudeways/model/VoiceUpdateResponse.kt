package com.gmwapp.dudeways.model

data class VoiceUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: VoiceData
)


data class VoiceData(
    val id: Int,
    val user_id: String,
    val name: String,
    val mobile: String,
    val voice: String,
    val status: Int,
    val datetime: String
)
