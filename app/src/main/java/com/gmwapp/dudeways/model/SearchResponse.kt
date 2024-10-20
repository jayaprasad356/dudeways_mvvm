package com.gmwapp.dudeways.model

data class SearchResponse(
    val success: Boolean,
    val message: String,
    val total: String,
    val data: ArrayList<SearchModel>
)

data class SearchModel(
    var id: Int? = 0,
    var name: String? = "",
    var unique_name: String? = "",
    var email: String? = "",
    var mobile: String? = "",
    var gender: String? = "",
    var profile: String? = "",
    var cover_img: String? = "",
    var datetime: String? = "",
    var age: String? = "",
//    var distance: String? = null
    var verified: String? = "",
    var friend: String? = "",
    var introduction: String? = ""

)
