package com.gmwapp.dudeways.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.gmwapp.dudeways.callback.ChatSentCallback
import com.gmwapp.dudeways.model.AddChatResponse
import com.gmwapp.dudeways.model.AddPointsResponse
import com.gmwapp.dudeways.model.BackImageResponse
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.ChatList
import com.gmwapp.dudeways.model.ChatResponse
import com.gmwapp.dudeways.model.FontImageResponse
import com.gmwapp.dudeways.model.OtherUserDetailResponse
import com.gmwapp.dudeways.model.PlanListResponse
import com.gmwapp.dudeways.model.PurchaseResponse
import com.gmwapp.dudeways.model.RewardResponse
import com.gmwapp.dudeways.model.SelfiImageResponse
import com.gmwapp.dudeways.repositories.ChatRepositories
import com.gmwapp.dudeways.workers.MessageSendWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(val chatRepositories: ChatRepositories) : ViewModel() {

    val isLoading = MutableLiveData(false)
    val chatLiveData = MutableLiveData<ChatResponse>()
    val unreadMessageLiveData = MutableLiveData<BaseResponse>()
    val readChatLiveData = MutableLiveData<BaseResponse>()
    val purchaseLiveData = MutableLiveData<PurchaseResponse>()
    val addPurchaseLiveData = MutableLiveData<RewardResponse>()
    val spinPointsLiveData = MutableLiveData<RewardResponse>()
    val addPointsLiveData = MutableLiveData<AddPointsResponse>()
    val otherUserDetailLiveData = MutableLiveData<OtherUserDetailResponse>()
    val planLiveData = MutableLiveData<PlanListResponse>()
    val paymentImageLiveData = MutableLiveData<BaseResponse>()
    val deleteChatLiveData = MutableLiveData<BaseResponse>()
    val addChatLiveData = MutableLiveData<AddChatResponse>()
    val addLocalChatLiveData = MutableLiveData<ChatList>()
    val selfiImageLiveData = MutableLiveData<SelfiImageResponse>()
    val fontImageLiveData = MutableLiveData<FontImageResponse>()
    val backImageLiveData = MutableLiveData<BackImageResponse>()

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
            chatRepositories.getReadChats(userId, chatUserId, msgSeen).let {
                if (it.body() != null) {
                    readChatLiveData.postValue(it.body())
                } else {
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

    fun addPoints(
        buyerName: String, amount: String,
        email: String, phone: String, purpose: String
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.addPoints(buyerName, amount, email, phone, purpose).let {
                if (it.body() != null) {
                    addPointsLiveData.postValue(it.body() as AddPointsResponse)
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

    fun spinPoints(userId: String, points: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.spinPoint(userId, points).let {
                if (it.body() != null) {
                    spinPointsLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun otherUserDetail(userId: String, otherUserId: String) {
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

    fun getPlan(userId: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.getPlan(userId).let {
                if (it.body() != null) {
                    planLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun getPaymentImage(uid: RequestBody, image: MultipartBody.Part?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.getPaymentImage(uid, image).let {
                if (it.body() != null) {
                    paymentImageLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun selfiImage(uid: RequestBody, image: MultipartBody.Part?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.selfiImageResponse(uid, image).let {
                if (it.body() != null) {
                    selfiImageLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun fontImage(uid: RequestBody, image: MultipartBody.Part?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.verifyFontImage(uid, image).let {
                if (it.body() != null) {
                    fontImageLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun backImage(uid: RequestBody, image: MultipartBody.Part?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.verifyBackImage(uid, image).let {
                if (it.body() != null) {
                    backImageLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun deleteChat(uid: String, chatUserId: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            chatRepositories.deleteChat(uid, chatUserId).let {
                if (it.body() != null) {
                    deleteChatLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun addChat(
        context: Context, userId: String, chatUserId: String,
        unRead: String, msgSeen: String, message: String, chatList: ChatList,
        onChatSentCallback: ChatSentCallback
    ) {

        viewModelScope.launch {
            addLocalChatLiveData.postValue(chatList)
            chatRepositories.addChat(userId, chatUserId, unRead, msgSeen, message, object :
                Callback<AddChatResponse> {
                override fun onResponse(
                    call: Call<AddChatResponse>,
                    response: Response<AddChatResponse>
                ) {
                    if (response.body() != null) {
                        addChatLiveData.postValue(response.body())
                        onChatSentCallback.onChatSent()
                    } else {
                        if (response.code() != null) {
                            addChatLiveData.postValue(
                                AddChatResponse(
                                    false,
                                    errorCode = response.code(),
                                    message = "",
                                    chat_status = "0"
                                )
                            )
                        }
                        val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                        val data: Data = Data.Builder()
                            .putString("userId", userId)
                            .putString("chatUserId", chatUserId)
                            .putString("unRead", unRead)
                            .putString("msgSeen", msgSeen)
                            .putString("message", message)
                            .build()
                        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
                            MessageSendWorker::class.java
                        )
                            .setInputData(data)
                            .setConstraints(constraints)
                            .build()
                        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)

                    }
                }

                override fun onFailure(call: Call<AddChatResponse>, t: Throwable) {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val data: Data = Data.Builder()
                        .putString("userId", userId)
                        .putString("chatUserId", chatUserId)
                        .putString("unRead", unRead)
                        .putString("msgSeen", msgSeen)
                        .putString("message", message)
                        .build()
                    val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
                        MessageSendWorker::class.java
                    )
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
                }
            })

        }
    }

}

