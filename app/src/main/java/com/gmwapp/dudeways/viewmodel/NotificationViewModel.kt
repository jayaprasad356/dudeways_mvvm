package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.NotificationResponse
import com.gmwapp.dudeways.repositories.NotificationRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(val notificationRepositories: NotificationRepositories) :
    ViewModel() {

    val isLoading = MutableLiveData(false)
    val notificationLiveData = MutableLiveData<NotificationResponse>()
    val updateNotificationLiveData = MutableLiveData<BaseResponse>()

    fun getNotifications(userId: String, offset: String, limit: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            notificationRepositories.getNotifications(userId, offset, limit).let {
                if (it.body() != null) {
                    notificationLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun updateNotifications(userId: String, msgNotify:String,addFriendNotify:String,
                            viewNotify:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            notificationRepositories.updateNotification(userId, msgNotify,addFriendNotify,viewNotify).let {
                if (it.body() != null) {
                    updateNotificationLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }



}