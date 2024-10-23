package com.gmwapp.dudeways.model

data class AddChatResponse(
    val success: Boolean,
    val message: String,
    val errorCode: Int?,
    val chat_status: String
)
