package com.gmwapp.dudeways.model

data class HomeUserResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<HomeUserlist>
)

data class HomeUserlist(
    var id: String? = "",
    var name: String? = "",
    var unique_name: String? = "",
    var email: String? = "",
    var mobile: String? = "",
    var gender: String? = "",
    var profile: String? = "",
    var cover_img: String? = "",
    var online_status: String? = ""
)