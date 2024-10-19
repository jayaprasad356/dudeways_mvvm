package com.gmwapp.dudeways.model

data class ChatResponse(
    val success: Boolean,
    val message: String,
    val total: Int,
    val data:ArrayList<ChatModel>
)

data class ChatModel(

    var id: String? = "",
    var user_id: String? = "",
    var name: String? = "",
    var profile: String? = "",
    var chat_user_id: String? = "",
    var online_status: String? = "",
    var unique_name: String? = "",
    var latest_message: String? = "",
    var latest_msg_time: String? = "",
    var msg_seen: String? = "",
    var datetime: String? = "",
    var updated_at: String? = "",
    var created_at: String? = "",
    var friend: String? = "",
    var verified: String? = "",
    var unread: String? = "",
    var points: String? = "",
)