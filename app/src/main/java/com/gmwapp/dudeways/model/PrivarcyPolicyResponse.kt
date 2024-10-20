package com.gmwapp.dudeways.model

data class PrivarcyPolicyResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<PrivarcyPolicyModel>
)

data class PrivarcyPolicyModel(
    val privacy_policy: String
)