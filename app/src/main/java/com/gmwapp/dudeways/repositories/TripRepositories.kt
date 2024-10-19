package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import javax.inject.Inject

class TripRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun updateTripImage(
        tripId: RequestBody,
        tripImage: MultipartBody.Part?
    ) = apiService.updateTripImage(
        tripId, tripImage
    )

    suspend fun addTrip(
        userId: String, tripType: String,
        fromDate: String, toDate: String, title: String,
        description: String, location: String,
        image: String
    ) = apiService.addTrip(
        userId, tripType, fromDate, toDate, title, description, location, image
    )

    suspend fun myTrip(userId:String,offset:String,limit:String)=apiService.myTrip(
        userId,offset,limit
    )

    suspend fun deleteTrip(tripId:String)=apiService.deleteTrip(tripId)
}