package com.gmwapp.dudeways.application

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface ChatDao {

    @Insert
    suspend fun insertChat(chat: ChatEntity)

    @Update
    suspend fun updateChat(chat: ChatEntity)

    @Query("SELECT * FROM chat_messages WHERE isSent = 0")
    suspend fun getUnsyncedChats(): List<ChatEntity>

    @Query("DELETE FROM chat_messages WHERE isSent = 1")
    suspend fun deleteSentChats()
}


