package com.gmwapp.dudeways.application

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey val chatID: String,
    val senderID: String,
    val receiverID: String,
    val message: String,
    val timestamp: Long,
    val isSent: Boolean = false  // Flag to mark if the message is sent
)

