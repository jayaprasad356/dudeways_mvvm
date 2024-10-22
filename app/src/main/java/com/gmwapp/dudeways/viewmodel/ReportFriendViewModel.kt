package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.ConnectResponse
import com.gmwapp.dudeways.repositories.FriendRespositories
import com.gmwapp.dudeways.repositories.ReportRespositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportFriendViewModel @Inject constructor(val reportFriendViewModel: ReportRespositories) :
    ViewModel() {

    val isLoading = MutableLiveData(false)
    val reportFrientLiveData = MutableLiveData<BaseResponse>()


    fun reportFriend(userId: String, friendUserId: String,message: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            reportFriendViewModel.reportFriends(userId, friendUserId,message).let {
                if (it.body() != null) {
                    reportFrientLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }



}