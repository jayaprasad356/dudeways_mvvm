package com.gmwapp.dudeways.model

data class NotificationResponse(
    val success: Boolean,
    val message: String,
    val total: String,
    val data: ArrayList<NotificationModel>
)

data class NotificationModel(
    var id: Int? = 0,
    var user_id: Int? = 0,
    var notify_user_id: String? = "",
    var name: String? = "",
    var profile: String? = "",
    var cover_img: String? = "",
    var message: String? = "",
    var datetime: String? = "",
    var time: String? = "",
    var date: String? = "",
    var updated_at: String? = "",
    var created_at: String? = "",
    var unique_name: String? = "",
    var verified: String? = "",
    var gender: String? = "",
)
