package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.AddTripResponse
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.MyTripResponse
import com.gmwapp.dudeways.repositories.TripRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(val tripRepositories: TripRepositories) : ViewModel() {

    val isLoading = MutableLiveData(false)
    val tripImageLiveData = MutableLiveData<BaseResponse>()
    val addTripLiveData = MutableLiveData<AddTripResponse>()
    val myTripLiveData = MutableLiveData<MyTripResponse>()
    val deleteTripLiveData = MutableLiveData<BaseResponse>()

    fun updateTripImage(tripId: RequestBody, tripImage: MultipartBody.Part?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            tripRepositories.updateTripImage(tripId, tripImage).let {
                if (it.body() != null) {
                    tripImageLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun addTrip(
        userId: String, tripType: String,
        fromDate: String, toDate: String, title: String,
        description: String, location: String,
        image: String
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            tripRepositories.addTrip(
                userId,
                tripType,
                fromDate,
                toDate,
                title,
                description,
                location,
                image
            ).let {
                if (it.body() != null) {
                    addTripLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun myTrip(userId: String, offset: String, limit: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            tripRepositories.myTrip(userId, offset, limit).let {
                if (it.body() != null) {
                    myTripLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun deleteTrip(tripId:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            tripRepositories.deleteTrip(tripId).let {
                if (it.body() != null) {
                    deleteTripLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }
}