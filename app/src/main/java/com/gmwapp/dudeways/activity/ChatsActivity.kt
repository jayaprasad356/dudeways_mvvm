package com.gmwapp.dudeways.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.OnMessagesFetchedListner
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.ChatAdapter
import com.gmwapp.dudeways.databinding.ActivityChatsBinding
import com.gmwapp.dudeways.extentions.fetchMessages
import com.gmwapp.dudeways.extentions.logError
import com.gmwapp.dudeways.extentions.logInfo
import com.gmwapp.dudeways.extentions.observeUserStatus
import com.gmwapp.dudeways.extentions.playReceiveTone
import com.gmwapp.dudeways.model.ChatList
import com.gmwapp.dudeways.model.ChatModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.AddFriendViewModel
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatsActivity : AppCompatActivity(), OnMessagesFetchedListner {

    lateinit var binding: ActivityChatsBinding
    lateinit var mContext: ChatsActivity
    lateinit var activity: Activity
    lateinit var session: Session
    private val firebaseDatabase: FirebaseDatabase =
        Firebase.database("https://dudeways-c8f31-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val databaseReference: DatabaseReference = firebaseDatabase.reference
    private var chatReference: DatabaseReference? = null
    private var chatAdapter: ChatAdapter? = null
    private var messages = mutableListOf<ChatList?>()
    private var senderId = ""
    private var receiverId = ""
    private var senderName: String? = null
    private var receiverName: String? = null

    private lateinit var soundPool: SoundPool
    private var sentTone: Int = 0
    private var receiveTone: Int = 0
    private var friend_verified = ""
    private var isConversationsFetching: Boolean = true
    private lateinit var typingStatusReference: DatabaseReference
    private var lastMessageId: String? = null


    var gender = ""
    var verified = ""
    private val viewModel: AddFriendViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    var userId: String? = ""
    var chatId: String? = ""


    private fun handleDeepLink(intent: Intent?) {
        val action = intent?.action
        val data: Uri? = intent?.data

        if (Intent.ACTION_VIEW == action && data != null) {
            // Handle the deep link
            userId = data.getQueryParameter("userid")
            chatId = data.getQueryParameter("chatid")
//            val linkFriendVerified = data.getQueryParameter("friend_verified")
//            val linkSenderName = data.getQueryParameter("senderName")
//            val linkReceiverName = data.getQueryParameter("receiverName")


            if (userId != null && chatId != null) {
                // Display the extracted user ID and chat ID in a toast message
                //  Toast.makeText(this, "User ID: $userId, Chat ID: $chatId", Toast.LENGTH_SHORT).show()

                chatViewModel.otherUserDetail(userId.toString(), chatId.toString())


                // You can now use these variables within the current activity as needed
            } else {
                Toast.makeText(this, "Missing one or more query parameters", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            // Handle the case when the intent does not contain a deep link
            // Get data from the intent extras
            senderId = session.getData(Constant.USER_ID).toString()
            receiverId = intent?.getStringExtra("chat_user_id").toString()
            friend_verified = intent?.getStringExtra("friend_verified").toString()
            senderName = session.getData(Constant.UNIQUE_NAME)
            receiverName = intent?.getStringExtra("unique_name").toString()
            binding.tvName.text = intent!!.getStringExtra("name")
            chatViewModel.getReadChats(
                session.getData(Constant.USER_ID)
                    .toString(), receiverId, "0"
            )


            chatReference =
                databaseReference.child("CHATS_V2").child(senderName!!).child(receiverName!!)
            typingStatusReference = firebaseDatabase.getReference("typing_status/$senderId")
            // You can now use these variables or proceed to open a new activity if needed


            gender = session.getData(Constant.GENDER).toString()
            verified = session.getData(Constant.VERIFIED).toString()


//            if (friend_verified == "1") {
//                binding.tvAbout.visibility = View.VISIBLE
//                binding.ivVerified.visibility = View.VISIBLE
//            } else {
//                binding.tvAbout.visibility = View.VISIBLE
//                binding.ivVerified.visibility = View.GONE
//            }

            Glide.with(this)
                .load(session.getData("reciver_profile"))
                .placeholder(R.drawable.profile_placeholder)
                .into(binding.ivProfile)

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chats)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        activity = this
        session = Session(activity)
        handleDeepLink(intent)


    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) { // keyboard is opened
                binding.RVChats.postDelayed({
                    //here
                    //     binding.RVChats.smoothScrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
                }, 100)
            }
        }

        binding.messageEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                typingStatusReference =
                    firebaseDatabase.getReference("typing_status/${senderId}_to_${receiverId}")
                typingStatusReference.setValue(s.toString().isNotEmpty())
            }


            override fun afterTextChanged(s: Editable?) {}
        })


        /*binding.ivProfile.setOnClickListener{
            val intent = Intent(activity, ProfileinfoActivity::class.java)
            intent.putExtra("chat_user_id", receiverId!!)
            activity.startActivity(intent)
        }*/

        binding.sendButton.setOnClickListener {

        }

        binding.ivMore.setOnClickListener {
            showPopupMenu()
        }
        fetchMessages(chatReference, this@ChatsActivity) {
            isConversationsFetching = it
        }


        chatReference?.addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatModel = snapshot.getValue(ChatList::class.java)
                    logInfo(CHATS_ACTIVITY, "from firebase child added - $chatModel")
                    onMessageAdded(chatModel)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val chatModel = snapshot.getValue(ChatList::class.java)
                    onMessageRemoved(chatModel)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Not implemented
                }

                override fun onCancelled(error: DatabaseError) {
                    // Not implemented
                }
            }
        )

        observeTypingStatus(firebaseDatabase, receiverId)
        setUserStatus(firebaseDatabase, senderId, true)
        observeUserStatus(firebaseDatabase, receiverId)


    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        chatViewModel.otherUserDetailLiveData.observe(this, Observer {
            if (it.success) {
                // Assign values to variables
                senderId = userId.toString()
                receiverId = chatId.toString()
                friend_verified = it.data.verified.toString()
                senderName = session.getData(Constant.UNIQUE_NAME).toString()
                receiverName = it.data.unique_name.toString()
                binding.tvName.text = it.data.name.toString()
                chatViewModel.getReadChats(
                    session.getData(Constant.USER_ID)
                        .toString(),
                    receiverId, "0"
                )

                chatReference =
                    databaseReference.child("CHATS_V2").child(senderName!!).child(receiverName!!)
                typingStatusReference = firebaseDatabase.getReference("typing_status/$senderId")


                gender = session.getData(Constant.GENDER).toString()
                verified = session.getData(Constant.VERIFIED).toString()


//                        if (friend_verified == "1") {
//                            binding.tvAbout.visibility = View.VISIBLE
//                            binding.ivVerified.visibility = View.VISIBLE
//                        } else {
//                            binding.tvAbout.visibility = View.VISIBLE
//                            binding.ivVerified.visibility = View.GONE
//                        }

                Glide.with(this)
                    .load(it.data.profile)
                    .placeholder(R.drawable.profile_placeholder)
                    .into(binding.ivProfile)


                onResume()


                // Toast.makeText(activity, friend, Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        chatViewModel.readChatLiveData.observe(this, Observer {
            if (it.success) {

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.addFriendLiveData.observe(this, Observer {
            if (it.success) {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setUserStatus(
        firebaseDatabase: FirebaseDatabase,
        senderID: String,
        isOnline: Boolean
    ) {
        val userStatusRef = firebaseDatabase.getReference("user_status/$senderID")

        val status = if (isOnline) {
            mapOf("status" to "online")

        } else {
            mapOf("status" to "offline", "last_seen" to System.currentTimeMillis())
        }

        userStatusRef.setValue(status).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logInfo(CHATS_ACTIVITY, "User status updated: $status")
            } else {
                logError(CHATS_ACTIVITY, "Failed to update user status: ${task.exception?.message}")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        sentTone = soundPool.load(this, R.raw.sent_new, 1)

    }


    private fun ChatsActivity.observeTypingStatus(
        firebaseDatabase: FirebaseDatabase,
        receiverID: String,
    ) {
        val typingStatusReference =
            firebaseDatabase.getReference("typing_status/${receiverID}_to_${senderId}")
        typingStatusReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isTyping = snapshot.getValue(Boolean::class.java) ?: false
                binding.typingStatus.visibility = if (isTyping) View.VISIBLE else View.GONE
                binding.tvLastSeen.visibility = if (isTyping) View.GONE else View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                logError(CHATS_ACTIVITY, "Error observing typing status: ${error.message}")
            }
        })
    }

    private fun updateSeenStatus(snapshot: DataSnapshot) {
        val chatModel = snapshot.getValue(ChatList::class.java)
        chatModel?.let {
            Log.d(
                "SeenStatus",
                "Processing message: ${it.message}, ID: ${snapshot.key}, ReceiverID: ${it.receiverID}, SenderID: $senderId, MsgSeen: ${it.msgSeen}"
            )

            // Check if this is the most recent message
            if (it.receiverID == senderId && it.msgSeen == true) {
                if (snapshot.key == lastMessageId) {
                    Log.d("SeenStatus", "Last message seen by receiver: ${it.message}")
                    makeToast("Your message has been seen.")
                } else {
                    Log.d("SeenStatus", "Message is not the last one.")
                }
            }

            // Update lastMessageId with the current message ID
            lastMessageId = snapshot.key
        } ?: Log.d("SeenStatus", "ChatModel is null for snapshot: ${snapshot.key}")
    }

    private fun makeToast(message: String) {
        // Ensure to call this on the main thread if it's not already
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        chatViewModel.getReadChats(
            session.getData(Constant.USER_ID)
                .toString(),
            receiverId, "0"
        )
        // Set typing status to false
        onStop()


        // Call the super method to handle the back press action
        super.onBackPressed()
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, binding.ivMore)
        popupMenu.inflate(R.menu.chat_options_menu)

        isBlocked(senderId, receiverId) { blocked ->
            val blockMenuItem = popupMenu.menu.findItem(R.id.menu_block_chat)
            blockMenuItem.title = if (blocked) "Unblock User" else "Block User"

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_block_chat -> {
                        updateBlockStatus(senderId, receiverId, !blocked)
                        true
                    }

                    R.id.menu_clear_chat -> {
                        clearChatInFirebase(senderName ?: "", receiverName ?: "", receiverId)
                        true
                    }

                    R.id.menu_report -> {
                        viewModel.addFriend(
                            session.getData(Constant.USER_ID).toString(),
                            receiverId, "2"
                        )
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun updateBlockStatus(senderId: String, receiverId: String, isBlocked: Boolean) {
        val blockReference =
            databaseReference.child("blocked_users").child(senderId).child(receiverId)
        blockReference.setValue(isBlocked).addOnSuccessListener {
            val message = if (isBlocked) {
                "User has been blocked"
            } else {
                "User has been unblocked"
            }
            makeToast(message)
        }.addOnFailureListener {
            makeToast("Failed to update block status")
        }
    }

    private fun isBlocked(senderId: String, receiverId: String, callback: (Boolean) -> Unit) {
        val blockReference =
            databaseReference.child("blocked_users").child(senderId).child(receiverId)
        blockReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isBlocked = snapshot.getValue(Boolean::class.java) ?: false
                callback(isBlocked)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    private fun clearChatInFirebase(senderName: String, receiverName: String, receiverID: String) {
        val senderChatReference =
            databaseReference.child("CHATS_V2").child(senderName).child(receiverName)
        val receiverChatReference =
            databaseReference.child("CHATS_V2").child(receiverName).child(senderName)

        senderChatReference.removeValue().addOnSuccessListener {
            deleteChat(receiverID)
            makeToast("Chat cleared successfully")
            messages.clear()
            chatAdapter?.notifyDataSetChanged()
        }.addOnFailureListener {
            makeToast("Failed to clear chat")
        }

        receiverChatReference.removeValue().addOnFailureListener {
            makeToast("Failed to clear chat for receiver")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessagesFetched(conversations: MutableList<ChatList?>) {
        var lastDisplayedDateTime: Long? = null
        messages = conversations
        if (messages.isNotEmpty()) {
            messages.sortBy { it?.dateTime }
            initializeRecyclerView(messages)

            binding.RVChats.scrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
        } else {
            //Display empty conversation placeholder.
            logInfo("conversations", "Conversations are empty.")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageAdded(chatModel: ChatList?) {
        chatModel?.let { nonEmptyChatModel ->
            when {
                messages.isEmpty() -> {
                    logInfo("$CHATS_ACTIVITY onMessageAdded", "Message added are empty")
                    messages.add(nonEmptyChatModel)
                    initializeRecyclerView(messages)
                    if (messages.any { it?.sentBy != session.getData(Constant.NAME) }) {

                        playReceiveTone(
                            soundPool,
                            receiveTone
                        )
                    } else {

                    }
                }

                messages.none { existingChatModel -> existingChatModel?.chatID == chatModel.chatID } -> {
                    Log
                        .e("ADDED_NON_EMPTY_CHAT_MODEL", nonEmptyChatModel.toString())
                    messages.add(nonEmptyChatModel)
                    if (messages.any { it?.sentBy != session.getData(Constant.NAME) }) {
                        playReceiveTone(
                            soundPool,
                            receiveTone
                        )
                    }
                    messages.sortBy { it?.dateTime }
                    initializeRecyclerView(messages)
                    receiverName?.let { nonEmptyReceiverName ->
                        senderName?.let { nonEmptySenderName ->
                            nonEmptyChatModel.chatID?.let { nonEmptyChatID ->
                                //Todo : This causing the bug in the app. Fix this to enable tick functionality
//                                updateMessageSeenStatus(
//                                    receiverName = nonEmptyReceiverName,
//                                    senderName = nonEmptySenderName,
//                                    chatID = nonEmptyChatID
//                                )
                            }
                        }
                    }
                    binding.RVChats.smoothScrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
                    logInfo("$CHATS_ACTIVITY onMessageAdded", "Message added: $chatModel")
                    chatViewModel.getReadChats(
                        session.getData(Constant.USER_ID)
                            .toString(), receiverId, "0"
                    )

                }

                else -> {
                    logInfo("$CHATS_ACTIVITY onMessageAdded", "Duplicate message ignored")
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageChanged(chatModel: ChatList?) {
        logInfo(CHATS_ACTIVITY, "Message changed - $chatModel")
        chatAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMessageRemoved(chatModel: ChatList?) {
        messages.remove(chatModel)
        chatAdapter?.notifyDataSetChanged()
        logInfo("$CHATS_ACTIVITY onMessageRemoved", "Message removed: $chatModel")
    }

    override fun onError(errorMessage: String) {
        makeToast("Error: $errorMessage")
        logError("$CHATS_ACTIVITY onError", "Error: $errorMessage")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initializeRecyclerView(conversations: MutableList<ChatList?>) {
        chatAdapter = ChatAdapter(
            conversations,
            onClick = { /* Handle message click */ },
            session,
            onMessageDelete = { chatModel ->
                // Handle message deletion here, e.g., remove from database
                // You might need to implement logic to actually delete the message from your data source
                // For example:
                // chatViewModel.deleteMessage(chatModel)
            }
        )
        binding.RVChats.apply {
            layoutManager = LinearLayoutManager(this@ChatsActivity)
            adapter = chatAdapter
            scrollToPosition(
                chatAdapter?.itemCount?.minus(1) ?: 0
            ) // Ensure scrolling to the last message
            invalidate()
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        // Set user offline status with last seen time
        setUserStatus(
            firebaseDatabase,
            senderId,
            false
        )
    }

    override fun onStop() {
        super.onStop()
        typingStatusReference =
            firebaseDatabase.getReference("typing_status/${senderId}_to_${receiverId}")
        typingStatusReference.setValue(false)
        setUserStatus(firebaseDatabase, senderId, false)
    }


    override fun onStart() {
        super.onStart()
        // Set user online status
        setUserStatus(firebaseDatabase, senderId, true)
    }

    fun showCustomDialog(context: Context) {
        // Inflate the custom layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)

        // Create an AlertDialog builder and set the custom layout
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)

        // Create the dialog
        val dialog = builder.create()

        val btnVerify = dialogView.findViewById<Button>(R.id.btnVerify)

        // Set up click listeners for the buttons
        btnVerify.setOnClickListener {

            val proof1 = session.getData(Constant.SELFIE_IMAGE)
            val proof2 = session.getData(Constant.FRONT_IMAGE)
            val proof3 = session.getData(Constant.BACK_IMAGE)
            val status = session.getData(Constant.STATUS)
            val payment_status = session.getData(Constant.PAYMENT_STATUS)


            // if proof 1 2 3 is empty
            if (proof1?.isEmpty() == true || proof2?.isEmpty() == true || proof3?.isEmpty() == true) {
                val intent = Intent(activity, IdverficationActivity::class.java)
                startActivity(intent)
            } else if (payment_status == "0") {
                val intent = Intent(activity, PurchaseverifybuttonActivity::class.java)
                startActivity(intent)
            } else if (status == "0") {
                val intent = Intent(activity, Stage4Activity::class.java)
                startActivity(intent)
            } else if (status == "1") {
                val intent = Intent(activity, VerifiedActivity::class.java)
                startActivity(intent)
            }
            dialog.dismiss() // Dismiss the dialog
        }
        // Show the dialog
        dialog.show()


    }

}

const val CHATS_ACTIVITY = "ChatsActivity"
