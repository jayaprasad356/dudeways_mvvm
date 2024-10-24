package com.gmwapp.dudeways.extentions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.media.SoundPool
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.gmwapp.dudeways.OnMessagesFetchedListner
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.CHATS_ACTIVITY
import com.gmwapp.dudeways.activity.ChatsActivity
import com.gmwapp.dudeways.activity.FreePointsActivity
import com.gmwapp.dudeways.activity.PurchasepointActivity
import com.gmwapp.dudeways.adapter.ChatAdapter
import com.gmwapp.dudeways.model.ChatList
import com.gmwapp.dudeways.utils.Constant
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONException
import org.json.JSONObject
import kotlin.random.Random


 var chat_status = ""
fun ChatsActivity.popUpMenu(
    senderID: String,
    receiverID: String,
    firebaseDatabase: FirebaseDatabase,
) {
    val popupMenu = PopupMenu(this, binding.ivMore)
    popupMenu.inflate(R.menu.chat_options_menu)
    isBlocked(
        senderID,
        receiverID,
        firebaseDatabase
    ) { blocked ->
        val blockMenuItem = popupMenu.menu.findItem(R.id.menu_block_chat)
        blockMenuItem.title = if (blocked) "Unblock User" else "Block User"

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_block_chat -> {
                    if (blocked) {
                        // Unblock user
                        updateBlockStatus(senderID, receiverID, firebaseDatabase, false)
                    } else {
                        // Block user
                        updateBlockStatus(senderID, receiverID, firebaseDatabase, true)
                    }
                    true
                }

                R.id.menu_clear_chat -> {
                    clearChatInFirebase(senderID, receiverID, firebaseDatabase.reference)
                    // Implement clear chat functionality if needed
                    true
                }

                R.id.menu_report -> {
                    report()
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
}

private fun ChatsActivity.isBlocked(
    senderID: String,
    receiverId: String,
    firebaseDatabase: FirebaseDatabase,
    onBlock: (Boolean) -> Unit
) {
    val blockStatusRef = firebaseDatabase.getReference("block_status")

    blockStatusRef.child(senderID).child(receiverId).get()
        .addOnSuccessListener { snapshot ->
            val isBlocked = snapshot.getValue(Boolean::class.java) ?: false
            onBlock(isBlocked)
        }
        .addOnFailureListener { exception ->
            logError(CHATS_ACTIVITY, "Error checking block status: ${exception.message}")
            onBlock(false) // Default to not blocked in case of error
        }
}

private fun ChatsActivity.updateBlockStatus(
    senderId: String,
    receiverId: String,
    firebaseDatabase: FirebaseDatabase,
    blocked: Boolean
) {
    val blockStatusRef = firebaseDatabase.getReference("block_status")

    // Update block status for sender -> receiver
    blockStatusRef.child(senderId).child(receiverId).setValue(blocked)
        .addOnSuccessListener {
            if (blocked) {
                logInfo(CHATS_ACTIVITY, "User blocked successfully")
            } else {
                logInfo(CHATS_ACTIVITY, "User unblocked successfully")
            }
        }
        .addOnFailureListener { exception ->
            logError(CHATS_ACTIVITY, "Failed to update block status: ${exception.message}")
        }

    // Update block status for receiver -> sender (optional)
    blockStatusRef.child(receiverId).child(senderId).setValue(blocked)
        .addOnSuccessListener {
            if (blocked) {
                logInfo(CHATS_ACTIVITY, "User blocked successfully (reverse)")
            } else {
                logInfo(CHATS_ACTIVITY, "User unblocked successfully (reverse)")
            }
        }
        .addOnFailureListener { exception ->
            logError(
                CHATS_ACTIVITY,
                "Failed to update block status (reverse): ${exception.message}"
            )
        }
}


fun ChatsActivity.report() {
    // Implement report functionality
}


@SuppressLint("NotifyDataSetChanged")
fun ChatsActivity.clearLocalMessages(
    messages: MutableList<ChatList?>,
    chatAdapter: ChatAdapter
) {
    messages.clear()
    chatAdapter.notifyDataSetChanged()
}

fun ChatsActivity.clearChatInFirebase(
    senderName: String,
    receiverName: String,
    databaseReference: DatabaseReference
) {
    logInfo(CHATS_ACTIVITY, "Attempting to clear chat for $senderName -> $receiverName")

    val senderChatRef = databaseReference.child("CHATS_V2").child(senderName).child(receiverName)
    senderChatRef.removeValue()
        .addOnSuccessListener {
            logInfo(CHATS_ACTIVITY, "Chat cleared successfully for sender: $senderName")
        }
        .addOnFailureListener { exception ->
            logError(CHATS_ACTIVITY, "Failed to clear chat for sender ($senderName -> $receiverName): ${exception.message}")
        }

    val receiverChatRef = databaseReference.child("CHATS_V2").child(receiverName).child(senderName)
    receiverChatRef.removeValue()
        .addOnSuccessListener {
            logInfo(CHATS_ACTIVITY, "Chat cleared successfully for receiver: $receiverName")
        }
        .addOnFailureListener { exception ->
            logError(CHATS_ACTIVITY, "Failed to clear chat for receiver ($receiverName -> $senderName): ${exception.message}")
        }
}


/**
 *  Set user status and updates to firebase
 */


/**
 *  Observes typing status and updates the UI accordingly.
 */


/**
 *  Observes user status and updates the UI accordingly.
 */
fun ChatsActivity.observeUserStatus(
    firebaseDatabase: FirebaseDatabase,
    receiverId: String
) {
    val userStatusRef = firebaseDatabase.getReference("user_status/$receiverId")

    userStatusRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val status = snapshot.child("status").getValue(String::class.java)
            val lastSeen = snapshot.child("last_seen").getValue(Long::class.java) ?: 0L

            if (status == "online") {
                binding.tvLastSeen.text = "Online"
                binding.IVOnlineStatus.visibility = View.VISIBLE
            }

            else{
                binding.tvLastSeen.text = ""
            }

//            } else {
//                binding.IVOnlineStatus.visibility = View.GONE
//                val currentTimeMillis = System.currentTimeMillis()
//                val timeAgoMillis = currentTimeMillis - lastSeen
//
//                val timeAgo: CharSequence = if (timeAgoMillis < DateUtils.DAY_IN_MILLIS) {
//                    DateUtils.getRelativeTimeSpanString(
//                        lastSeen,
//                        currentTimeMillis,
//                        DateUtils.MINUTE_IN_MILLIS,
//                        DateUtils.FORMAT_ABBREV_RELATIVE
//                    )
//                } else {
//                    val daysAgo = (timeAgoMillis / DateUtils.DAY_IN_MILLIS).toInt()
//                    when (daysAgo) {
//                        1 -> "1 day ago"
//                        else -> "$daysAgo days ago"
//                    }
//                }
//
//                binding.tvLastSeen.text = timeAgo
//            }
        }

        override fun onCancelled(error: DatabaseError) {
            logError(CHATS_ACTIVITY, "Failed to fetch user status: ${error.message}")
        }
    })
}

/**
 *  Fetch messages from Firebase
 */
fun ChatsActivity.fetchMessages(
    chatReference: DatabaseReference?,
    onMessagesFetchedListener: OnMessagesFetchedListner,
    isConversationsFetching: (Boolean) -> Unit
) {
    val conversations: MutableList<ChatList?> = mutableListOf()
    chatReference?.get()?.addOnSuccessListener { dataSnapshot ->
        if (dataSnapshot.exists()) {
            for (child in dataSnapshot.children) {
                val chatModel = child.getValue(ChatList::class.java)
                if (chatModel != null) {
                    logInfo(CHATS_ACTIVITY, "ChatModel: $chatModel")
                    conversations.add(chatModel)
                } else {
                    logError(CHATS_ACTIVITY, "Unable to load your conversations.")
                }
            }
            onMessagesFetchedListener.onMessagesFetched(conversations)
            isConversationsFetching(false)
        } else {
            logInfo(CHATS_ACTIVITY, "No messages found.")
        }
    }?.addOnFailureListener { exception ->
        logError(CHATS_ACTIVITY, "Error fetching messages: ${exception.message}")
        onMessagesFetchedListener.onError(exception.message.toString())
    }
}

/**
 *  Update the message for sender in firebase.
 */
fun ChatsActivity.updateMessagesForSender(
    databaseReference: DatabaseReference,
    senderID: String,
    receiverID: String,
    senderName: String,
    receiverName: String,
    message: String,
    soundPool: SoundPool,
    sentTone: Int
) {
    val chatID = Random.nextInt(100000, 999999).toString()
    val chatModel = ChatList(
        attachmentType = "TEXT",
        chatID = chatID,
        dateTime = Timestamp.now().toDate().time,
        message = message,
        msgSeen = false,
        receiverID = receiverID,
        senderID = senderID,
        type = "TEXT",
        sentBy = session.getData(Constant.NAME)
    )
 //   addChat(message,receiverID)

    if (session.getData(Constant.CHAT_STATUS) == "0") {

        val dialogView = activity.layoutInflater.inflate(R.layout.dialog_custom, null)

        val dialogBuilder = AlertDialog.Builder(activity)
            .setView(dialogView)
            .create()
        val title = dialogView.findViewById<TextView>(R.id.dialog_title)
        val btnPurchase = dialogView.findViewById<LinearLayout>(R.id.btnPurchase)
        val btnFreePoints = dialogView.findViewById<LinearLayout>(R.id.btnFreePoints)


        title.text = "You have ${session.getData(Constant.POINTS)} Points"

        btnPurchase.setOnClickListener {
            val intent = Intent(activity, PurchasepointActivity::class.java)
            activity.startActivity(intent)
            dialogBuilder.dismiss()
        }

        btnFreePoints.setOnClickListener {
            val intent = Intent(activity, FreePointsActivity::class.java)
            activity.startActivity(intent)
            dialogBuilder.dismiss()
        }


        dialogBuilder.show()

    }

    else {
        databaseReference.child("CHATS_V2")
        .child(senderName)
        .child(receiverName)
        .child(chatID)
        .setValue(chatModel)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateMessagesForReceiver(
                    senderID,
                    receiverID,
                    chatID,
                    senderName,
                    receiverName,
                    message,
                    databaseReference
                )
                playSentTone(soundPool, sentTone)
                logInfo(CHATS_ACTIVITY, "Message sent")
//                msg_seen()

            } else {
                logError(CHATS_ACTIVITY, "Failed to send message")
            }

        }
        }
}

/**
 *  Update the message for receiver in firebase.
 */
private fun ChatsActivity.updateMessagesForReceiver(
    senderID: String,
    receiverID: String,
    chatID: String,
    senderName: String,
    receiverName: String,
    message: String,
    databaseReference: DatabaseReference
) {
    val chatModel = ChatList(
        attachmentType = "TEXT",
        chatID = chatID,
        dateTime = Timestamp.now().toDate().time,
        message = message,
        msgSeen = false, // Initially set to false
        receiverID = senderID,
        senderID = receiverID,
        type = "TEXT",
        sentBy = session.getData(Constant.NAME)
    )

    databaseReference.child("CHATS_V2")
        .child(receiverName)
        .child(senderName)
        .child(chatID)
        .setValue(chatModel)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Log message successfully updated
                logInfo("updateMessages", "Message updated for receiver")
            // Listen for changes to msgSeen
            } else {
                logError("updateMessages", "Failed to send message: ${task.exception?.message}")
            }
        }
}






/**
 *  Play sent tone
 */
private fun ChatsActivity.playSentTone(
    soundPool: SoundPool,
    sentTone: Int
) {
    logInfo(CHATS_ACTIVITY, "Attempting to play sent tone")
    val result = soundPool.play(sentTone, 1f, 1f, 0, 0, 1f)
    if (result == 0) {
//        Toast.makeText(this, "Failed to play sent tone", Toast.LENGTH_SHORT).show()
        logError(CHATS_ACTIVITY, "Failed to play sent tone")
    } else {
       // Toast.makeText(this, "Sent tone played successfully", Toast.LENGTH_SHORT).show()
        logInfo(CHATS_ACTIVITY, "Sent tone played successfully")
    }
}

/**
 *  Play receive tone
 */
fun ChatsActivity.playReceiveTone(
    soundPool: SoundPool,
    receiveTone: Int
) {
    logInfo(CHATS_ACTIVITY, "Attempting to play receive tone")
    val result = soundPool.play(receiveTone, 1f, 1f, 0, 0, 1f)
    if (result == 0) {
        logError(CHATS_ACTIVITY, "Failed to play receive tone")
    } else {
        logInfo(CHATS_ACTIVITY, "Receive tone played successfully")
    }
}












