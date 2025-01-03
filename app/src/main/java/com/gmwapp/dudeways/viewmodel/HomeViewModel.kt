package com.gmwapp.dudeways.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.HomeResponse
import com.gmwapp.dudeways.model.HomeUserResponse
import com.gmwapp.dudeways.model.LoginResponse
import com.gmwapp.dudeways.model.ProffessionResponse
import com.gmwapp.dudeways.model.UserDataResponse
import com.gmwapp.dudeways.repositories.HomeRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val homeRepositories: HomeRepositories):ViewModel() {
    val isLoading = MutableLiveData(false)
    val locationLiveData = MutableLiveData<BaseResponse>()
    val userDetailLiveData = MutableLiveData<UserDataResponse>()
    val tripLiveData = MutableLiveData<HomeResponse>()
    val activeUserLiveData = MutableLiveData<HomeUserResponse>()

    fun updateLocation(userId:String,latitude:String,lognitude:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            homeRepositories.updateLocation(userId,latitude,lognitude).let {
                if (it.body() != null) {
                    locationLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun getUserDetails(userId:String,onlineStatus:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            homeRepositories.getUserDetails(userId,onlineStatus).let {
                if (it.body() != null) {
                    userDetailLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }



    fun getTrip(userId:String,type:String,offset:String,limit:String,date:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            homeRepositories.getTrip(userId,type,offset,limit,date).let {
                if (it.body() != null) {
                    tripLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun getActiveUser(userId:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            homeRepositories.getActiveUsers(userId).let {
                if (it.body() != null) {
                    activeUserLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


}