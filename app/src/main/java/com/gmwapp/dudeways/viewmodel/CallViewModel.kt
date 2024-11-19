package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.CallResponse
import com.gmwapp.dudeways.repositories.CallRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(val callRepositories: CallRepositories) :
    ViewModel() {

    val isLoading = MutableLiveData(false)
    val callLiveData = MutableLiveData<CallResponse>()
    val usercallLiveData = MutableLiveData<BaseResponse>()

    fun getRandomUser(userId: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            callRepositories.getRandomUser(userId).let {
                if (it.body() != null) {
                    callLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun setUserCall(
        userId: String,
        callUserId: String,
        startTime: String,
        endTime: String,
        duration: Int
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            callRepositories.setUserCall(userId,callUserId,startTime,endTime,duration).let {
                if (it.body() != null) {
                    usercallLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }








}