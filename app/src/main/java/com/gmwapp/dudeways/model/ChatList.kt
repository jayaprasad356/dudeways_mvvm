package com.gmwapp.dudeways.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class ChatList(
    val attachmentType: String? = "",
    val chatID: String? = "",
    val dateTime: Long? = Timestamp.now().toDate().time,
    val message: String? = "",
    val msgSeen: Boolean? = false,
    val receiverID: String? = "",
    val senderID: String? = "",
    val type: String? = "",
    val sentBy: String? = "",
    val typing: Boolean = false
)