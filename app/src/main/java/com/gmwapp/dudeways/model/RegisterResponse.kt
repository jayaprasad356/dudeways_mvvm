package com.gmwapp.dudeways.model

data class RegisterResponse(
    val success:Boolean,
    val message:String,
    val data:RegisterModel
)

data class RegisterNewResponse(
    val success:Boolean,
    val message:String,
    val data:RegisternewModel
)
data class RegisternewModel(
    val id: String,
    val name: String,
    val unique_name: String,
    val mobile: String,
    val age: String,
    val gender: String,
    val city: String,
    val language: String,
)


data class RegisterModel(
    val id: String,
    val name: String,
    val unique_name: String,
    val email: String,
    val mobile: String,
    val age: String,
    val dob: String,
    val gender: String,
    val state: String,
    val city: String,
    val profile:String,
    val profession: String,
    val refer_code: String,
    val referred_by: String,
    val introduction: String,
    val language: String,
)