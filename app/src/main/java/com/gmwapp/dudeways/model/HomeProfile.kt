package com.gmwapp.dudeways.model

data class HomeResponse(
    val success: Boolean,
    val message: String,
    val total: String,
    val data: ArrayList<HomeProfile>
)

data class HomeProfile(

    var id: String? = "",
    var name: String? = "",
    var user_id: String? = "",
    var unique_name: String? = "",
    var profile: String? = "",
    var planning: String? = "",
    var from_date: String? = "",
    var to_date: String? = "",
    var trip_title: String? = "",
    var trip_description: String? = "",
    var from_location: String? = "",
    var location: String? = "",
    var meetup_location: String? = "",
    var datetime: String? = "",
    var updated_at: String? = "",
    var created_at: String? = "",
    var friend: String? = "",
    var verified: String? = "",
    var gender: String? = "",
    var trip_image: String? = "",
    var distance: String? = "",
    var time: String? = "",
)