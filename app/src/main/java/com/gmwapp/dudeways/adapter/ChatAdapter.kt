package com.gmwapp.dudeways.adapter

import android.app.AlertDialog
import android.content.Intent.getIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ReceiverChatMessageBinding
import com.gmwapp.dudeways.databinding.SenderChatMessageBinding
import com.gmwapp.dudeways.model.ChatList
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.google.firebase.database.FirebaseDatabase
import com.zoho.livechat.android.utils.SalesIQCache.messages
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ChatAdapter(
    private val conversations: MutableList<ChatList?>,
    private val onClick: (ChatList) -> Unit,
    private var session: Session,
    private val onMessageDelete: (ChatList) -> Unit,
    private val receiverName: String // Add receiverName here
) : RecyclerView.Adapter<ChatAdapter.ItemHolder>() {

    inner class ItemHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatMessage: ChatList?, position: Int) {
            chatMessage?.let {
                val messageTime = it.dateTime?.toLong() ?: 0L
                val formattedTime = formatTime(messageTime)
                val formattedDate = formatDate(messageTime)

                val shouldShowDateHeader = shouldDisplayDateHeader(position, formattedDate)

                when (binding) {
                    is SenderChatMessageBinding -> {
                        binding.tvMessage.text = it.message
                        binding.tvTime.text = formattedTime
                        handleDateHeader(binding.tvDateHeader, shouldShowDateHeader, formattedDate)
                        Glide.with(binding.root.context).load(session.getData(Constant.PROFILE))
                            .placeholder(R.drawable.profile_placeholder).into(binding.ivUserProfile)
                        handleSeenStatus(binding.tvSeenStatus, position)

                        // Long press to show delete dialog
                        binding.root.setOnLongClickListener {
                            showDeleteConfirmationDialog(chatMessage)
                            true
                        }
                    }

                    is ReceiverChatMessageBinding -> {
                        binding.tvMessage.text = it.message
                        binding.tvTime.text = formattedTime
                        handleDateHeader(binding.tvDateHeader, shouldShowDateHeader, formattedDate)
                        Glide.with(binding.root.context).load(session.getData("reciver_profile"))
                            .placeholder(R.drawable.profile_placeholder).into(binding.ivUserProfile)

                        // Long press to show delete dialog
//                        binding.root.setOnLongClickListener {
//                            showDeleteConfirmationDialog(chatMessage)
//                            true
//                        }
                    }

                    else -> {}
                }
            }
        }

        private fun showDeleteConfirmationDialog(chatMessage: ChatList?) {
            val context = binding.root.context
            val dialogBuilder = AlertDialog.Builder(context)
                .setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes") { _, _ ->
                    chatMessage?.let {
                        deleteMessage(it)
                    }
                }
                .setNegativeButton("No", null)

            val dialog = dialogBuilder.create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(context, R.color.primary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(context, R.color.text_grey))
        }

        private fun deleteMessage(chatMessage: ChatList) {
            val senderName = session.getData(Constant.UNIQUE_NAME).toString() // Assuming this is the sender's name
            val chatId = chatMessage.chatID.toString()


           // Toast.makeText(binding.root.context, "$senderName", Toast.LENGTH_SHORT).show()
            // Reference to the message you want to delete
            val messageRef = FirebaseDatabase.getInstance().getReference("CHATS_V2")
                .child(senderName)
                .child(receiverName)
                .child(chatId)

            // Perform the delete operation
            messageRef.removeValue()
                .addOnSuccessListener {
                    // Message deleted successfully
                    println("Message deleted successfully: $chatId")

                    // Remove from local conversations list
                    val position = conversations.indexOfFirst { it?.chatID == chatId }
                    if (position != -1) {
                        conversations.removeAt(position) // Remove the message from the list
                        notifyItemRemoved(position) // Notify the adapter about item removal
                    } else {
                        println("Message not found in local list: $chatId")
                    }
                }
                .addOnFailureListener { error ->
                    // Handle the error
                    println("Error deleting message: ${error.message}")
                }
        }






        private fun handleDateHeader(dateHeader: TextView, shouldShow: Boolean, date: String) {
            if (shouldShow) {
                dateHeader.text = date
                dateHeader.visibility = View.VISIBLE
            } else {
                dateHeader.visibility = View.GONE
            }
        }

        private fun handleSeenStatus(tvSeenStatus: TextView, position: Int) {
            if (position == conversations.size - 1) {
                val msgSeen = session.getData(Constant.MSG_SEEN).toString()
                if (msgSeen == "0") {
                    tvSeenStatus.visibility = View.VISIBLE
                    tvSeenStatus.text = ""
                } else {
                    tvSeenStatus.visibility = View.GONE
                }
            } else {
                tvSeenStatus.visibility = View.GONE
            }
        }

        private fun shouldDisplayDateHeader(position: Int, currentDate: String): Boolean {
            if (position == 0) return true
            val previousMessage = conversations[position - 1]
            val previousDate = previousMessage?.dateTime?.toLong()?.let { formatDate(it) }
            return currentDate != previousDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding: ViewBinding = when (viewType) {
            0 -> ReceiverChatMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            1 -> SenderChatMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val chatMessage = conversations[position]
        holder.bind(chatMessage, position)
    }

    override fun getItemCount(): Int = conversations.size

    fun getItemInfo(position: Int): ChatList? = conversations[position]

    override fun getItemViewType(position: Int): Int {
        val chatMessage = conversations[position]
        val currentUser = session.getData(Constant.NAME)
        return if (chatMessage?.sentBy == currentUser) 1 else 0
    }

    private fun formatTime(timestamp: Long): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(timestamp)
    }

    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
    }

    private fun shouldDisplayDateHeader(position: Int, currentDate: String): Boolean {
        if (position == 0) return true
        val previousMessage = conversations[position - 1]
        val previousDate = previousMessage?.dateTime?.toLong()?.let { formatDate(it) }
        return currentDate != previousDate
    }

    fun addMessage(message: ChatList) {
        conversations.add(message)
        notifyItemInserted(messages.size - 1)
    }



}
