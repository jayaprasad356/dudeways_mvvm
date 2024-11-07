package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.ConnectResponse
import com.gmwapp.dudeways.repositories.FriendRespositories
import com.gmwapp.dudeways.repositories.StickerRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StickerViewModel @Inject constructor(
    val stickerRespositories: StickerRepositories
) :
    ViewModel() {

    val isLoading = MutableLiveData(false)
    val stickerLiveData = MutableLiveData<ConnectResponse>()




//    fun getSticker(userId: String) {
//        viewModelScope.launch {
//            isLoading.postValue(true)
//            stickerRespositories.getSticker(userId).let {
//                if (it.body() != null) {
//                    stickerRespositories.postValue(it.body())
//                    isLoading.postValue(false)
//                } else {
//                    isLoading.postValue(false)
//                }
//            }
//
//        }
//    }


}