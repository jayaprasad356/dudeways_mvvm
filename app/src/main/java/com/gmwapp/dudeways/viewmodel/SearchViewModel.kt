package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.SearchResponse
import com.gmwapp.dudeways.repositories.SearchRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(val searchRepositories: SearchRepositories)
    :ViewModel(){

    val isLoading = MutableLiveData(false)
    val userLiveData = MutableLiveData<SearchResponse>()

    fun getUsers(userId:String,offset:String,limit:String,gender:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            searchRepositories.getUsers(userId,offset,limit,gender).let {
                if (it.body() != null) {
                    userLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

}