package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.ChatResponse
import com.gmwapp.dudeways.model.OtherUserDetailModel
import com.gmwapp.dudeways.model.OtherUserDetailResponse
import com.gmwapp.dudeways.model.PurchaseResponse
import com.gmwapp.dudeways.model.RewardResponse
import com.gmwapp.dudeways.repositories.ChatRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(val chatRepositories: ChatRepositories) : ViewModel() {

    val isLoading = MutableLiveData(false)
    val chatLiveData = MutableLiveData<ChatResponse>()
    val unreadMessageLiveData = MutableLiveData<BaseResponse>()
    val readChatLiveData = MutableLiveData<BaseResponse>()
    val purchaseLiveData = MutableLiveData<PurchaseResponse>()
    val addPurchaseLiveData = MutableLiveData<RewardResponse>()
    val addPointsLiveData = MutableLiveData<PurchaseResponse>()
    val otherUserDetailLiveData = MutableLiveData<OtherUserDetailResponse>()

    fun getChat(userId: String, offset: String, limit: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.getChat(userId, offset, limit).let {
                if (it.body() != null) {
                    chatLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun getUnreadMessage(userId: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.getUnReadMessage(userId).let {
                if (it.body() != null) {
                    unreadMessageLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun getReadChats(userId: String, chatUserId: String, msgSeen: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.getReadChats(userId, chatUserId, msgSeen).let {
                if (it.body() != null) {
                    readChatLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun getPurchase(userId: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.getPurchase(userId).let {
                if (it.body() != null) {
                    purchaseLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun addPurchase(userId: String, points: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.addPurchase(userId, points).let {
                if (it.body() != null) {
                    addPurchaseLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun otherUserDetail(userId: String,otherUserId:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.otherUserDetails(userId, otherUserId).let {
                if (it.body() != null) {
                    otherUserDetailLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }
}