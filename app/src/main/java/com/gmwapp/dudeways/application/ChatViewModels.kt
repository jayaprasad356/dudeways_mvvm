package com.gmwapp.dudeways.application

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModels(application: Application) : AndroidViewModel(application) {

    private val chatDao: ChatDao = AppDatabase.getDatabase(application).chatDao()

    // Insert chat message into Room
    fun insertChat(chat: ChatEntity) {
        viewModelScope.launch {
            chatDao.insertChat(chat)
        }
    }

    // Update chat message status (sent or unsent)
    fun updateChat(chat: ChatEntity) {
        viewModelScope.launch {
            chatDao.updateChat(chat)
        }
    }

    // Get unsynced chat messages
    suspend fun getUnsyncedChats(): List<ChatEntity> {
        return chatDao.getUnsyncedChats()
    }

    // Clear sent chats
    fun clearSentChats() {
        viewModelScope.launch {
            chatDao.deleteSentChats()
        }
    }
}
