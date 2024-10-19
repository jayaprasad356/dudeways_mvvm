package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.ConnectResponse
import com.gmwapp.dudeways.repositories.FriendRespositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel @Inject constructor(val friendRespositories: FriendRespositories) :
    ViewModel() {

    val isLoading = MutableLiveData(false)
    val addFriendLiveData = MutableLiveData<BaseResponse>()
    val connectFriendLiveData = MutableLiveData<ConnectResponse>()

    fun addFriend(userId: String, friendUserId: String, friend: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            friendRespositories.addFriends(userId, friendUserId, friend).let {
                if (it.body() != null) {
                    addFriendLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun getConnect(userId: String, offset: String, limit: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            friendRespositories.getConnect(userId, offset, limit).let {
                if (it.body() != null) {
                    connectFriendLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


}