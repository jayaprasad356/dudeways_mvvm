package com.gmwapp.dudeways.model

data class TermsConditionResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<TermsConditionModel>
)

data class TermsConditionModel(
    val terms_conditions: String
)