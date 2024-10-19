package com.gmwapp.dudeways.model

data class ConnectResponse(
    val success: Boolean,
    val message: String,
    val total: String,
    val data: ArrayList<ConnectModel>
)

data class ConnectModel(
    var id: String? = "",
    var user_id: String? = "",
    var friend_user_id: String? = "",
    var name: String? = "",
    var introduction: String? = "",
    var gender: String? = "",
    var age: String? = "",
    var online_status: String? = "",
    var profile: String? = "",
    var last_seen: String? = "",
    var distance: String? = "",
    var status: String? = "",
    var datetime: String? = "",
    var updated_at: String? = "",
    var created_at: String? = "",
    var friend: String? = "",
    var unique_name: String? = "",
    var verified: String? = "",

)