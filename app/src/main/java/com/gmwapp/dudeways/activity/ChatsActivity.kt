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
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.OnMessagesFetchedListner
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.ChatAdapter
import com.gmwapp.dudeways.databinding.ActivityChatsBinding
import com.gmwapp.dudeways.extentions.chat_status
import com.gmwapp.dudeways.extentions.fetchMessages
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.extentions.logError
import com.gmwapp.dudeways.extentions.logInfo
import com.gmwapp.dudeways.extentions.observeUserStatus
import com.gmwapp.dudeways.extentions.playReceiveTone
import com.gmwapp.dudeways.extentions.updateMessagesForSender
import com.gmwapp.dudeways.model.ChatList
import com.gmwapp.dudeways.model.EmojiPopupHelper
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.AddFriendViewModel
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import com.gmwapp.dudeways.viewmodel.ReportFriendViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random


@AndroidEntryPoint
class ChatsActivity : BaseActivity(), OnMessagesFetchedListner {

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
    private lateinit var emojiPopupHelper: EmojiPopupHelper

    private lateinit var soundPool: SoundPool
    private var sentTone: Int = 0
    private var receiveTone: Int = 0
    private var friend_verified = ""
    private var isConversationsFetching: Boolean = true
    private lateinit var typingStatusReference: DatabaseReference
    private var lastMessageId: String? = null
    private var lastMessage: String = ""


    var gender = ""
    var verified = ""
    private val viewModel: AddFriendViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val reportviewModel: ReportFriendViewModel by viewModels()

    var userId: String? = ""
    var chatId: String? = ""

    private var rewardedAd: RewardedAd? = null
    private val adId = "ca-app-pub-8693482193769963/5956761344"

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
                session.getData(Constant.USER_ID).toString(), receiverId, "0"
            )


            chatReference = databaseReference.child("CHATS_V2").child(senderName!!).child(receiverName!!)
            typingStatusReference = firebaseDatabase.getReference("typing_status/$senderId")
            // You can now use these variables or proceed to open a new activity if needed


            gender = session.getData(Constant.GENDER).toString()
            verified = session.getData(Constant.VERIFIED).toString()


            val oppositeGender = intent?.getStringExtra("gender")

            if (oppositeGender == "male") {
                binding.ivProfile.borderColor = ContextCompat.getColor(activity, R.color.blue_200)
            }
            else {
                binding.ivProfile.borderColor = ContextCompat.getColor(activity, R.color.primary)
            }


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
        loadRewardedVideoAd()
    }

    private fun initUI() {
        activity = this
        session = Session(activity)
        handleDeepLink(intent)



        emojiPopupHelper = EmojiPopupHelper(this)

        emojiPopupHelper.setupEmojiPopup(binding.messageEdittext, binding.emojiButton, findViewById(R.id.main))


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
                       binding.RVChats.smoothScrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
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


        binding.ivProfile.setOnClickListener{
            val intent = Intent(activity, ProfileInfoActivity::class.java)
            intent.putExtra("chat_user_id", receiverId!!)
            activity.startActivity(intent)
        }


        isBlocked(senderId, receiverId) { isBlocked ->
            binding.sendButton.setOnClickListener {
                    if (isNetworkAvailable(mContext)) {
                        if (binding.messageEdittext.text.toString().trim().isEmpty()) {
                            Toast.makeText(mContext, "Please enter message", Toast.LENGTH_SHORT).show()
                        } else {
                            if (isBlocked) {
                                binding.sendButton.isClickable = true
                                makeToast("You cannot send messages to this user blocked.")
                            }
                            else{
                                chatViewModel.addChat(
                                    session.getData(Constant.USER_ID).toString(),
                                    receiverId.toString(),
                                    "1", "1",
                                    binding.messageEdittext.text.toString().trim(),
                                    ChatList(
                                        attachmentType = "TEXT",
                                        chatID = System.currentTimeMillis().toString()+"-"+Random.nextInt(100000, 999999).toString(),
                                        dateTime = Timestamp.now().toDate().time,
                                        message = binding.messageEdittext.text.toString().trim(),
                                        msgSeen = false,
                                        receiverID = this@ChatsActivity.receiverId,
                                        senderID = this@ChatsActivity.senderId,
                                        type = "TEXT",
                                        sentBy = session.getData(Constant.NAME)
                                    )
                                )
                            }


                        }

                    } else {
                        Toast.makeText(
                            mContext, getString(R.string.str_error_internet_connections),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }






        binding.ivMore.setOnClickListener {
            showPopupMenu()
        }
//
//        binding.ivphone.setOnClickListener {
//            val intent = Intent(activity, CallActivity::class.java).apply {
//                putExtra("TARGET_USER_ID", receiverId.toString())
//                putExtra("TARGET_USER_NAME", receiverName.toString())
//            }
//            activity?.startActivity(intent)
//        }


        fetchMessages(chatReference, this@ChatsActivity) {
            isConversationsFetching = it
        }


        chatReference?.addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatModel = snapshot.getValue(ChatList::class.java)
                    logInfo(CHATS_ACTIVITY, "from firebase child added - $chatModel")
                    if(!session.getData(Constant.NAME).equals(chatModel?.sentBy)) {
                        onMessageAdded(chatModel)
                    }
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

        chatViewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })
        chatViewModel.addLocalChatLiveData.observe(this, Observer {
            binding.sendButton.isClickable = true
            lastMessage = binding.messageEdittext.text.toString()
            binding.messageEdittext.text!!.clear()
            onMessageAdded(it)
        })





        chatViewModel.addChatLiveData.observe(this, Observer {
            if (it.success) {
                chat_status = it.chat_status
                session.setData(Constant.CHAT_STATUS, chat_status)
                session.setData(Constant.MSG_SEEN, "1")
                val message = lastMessage
                if (message.isNotEmpty()) {
                    isBlocked(senderId, receiverId)
                    {
                        isBlocked ->
                        if (isBlocked) {
                            binding.sendButton.isClickable = true
                            makeToast("You cannot send messages to this user blocked.")

                        } else {
                            binding.sendButton.isClickable = true
                            senderName?.let { sName ->
                                receiverName?.let { rName ->
                                    updateMessagesForSender(
                                        databaseReference = databaseReference,
                                        senderID = senderId,
                                        receiverID = receiverId,
                                        senderName = senderName!!,
                                        receiverName = receiverName!!,
                                        message = message,
                                        soundPool = soundPool,
                                        sentTone = sentTone
                                    )
                                //    binding.messageEdittext.text.clear()
                                } ?: logError(
                                    CHATS_ACTIVITY,
                                    "Unable to send your message."
                                )
                            } ?: logError(
                                CHATS_ACTIVITY,
                                "Unable to send your message."
                            )
                        }
                    }
                } else {
                    binding.sendButton.isClickable = true
                    makeToast("Enter text to send")
                }

                //   Toast.makeText(this, chat_status, Toast.LENGTH_SHORT).show()


            } else if(it.errorCode == 429) {
                makeToast(getString(R.string.please_try_again_later))
            } else if(it.errorCode !=null){
                makeToast(getString(R.string.please_try_again_later)+" "+it.errorCode)
            }
            else {
                binding.sendButton.isClickable = true

                chat_status = it.chat_status
                session.setData(Constant.CHAT_STATUS, chat_status)


                val dialogView =
                    activity.layoutInflater.inflate(R.layout.dilog_chat_point, null)

                val dialogBuilder = AlertDialog.Builder(activity)
                    .setView(dialogView)
                    .create()
                val title = dialogView.findViewById<TextView>(R.id.tvTitle)
                val btnPurchase =
                    dialogView.findViewById<MaterialButton>(R.id.btnPurchase)
                val tvDescription =
                    dialogView.findViewById<TextView>(R.id.tvDescription)
                val tvSubDescription =
                    dialogView.findViewById<TextView>(R.id.tvSubDescription)

                val btnFreePoints = dialogView.findViewById<MaterialButton>(R.id.btnFreePoints)


                tvDescription.text =
                    "Buy 100 points to chat with 10 female users for up to 10 hours."
                tvDescription.setTextColor(ContextCompat.getColor(activity, R.color.primary))

                tvSubDescription.visibility = View.GONE

                title.text = "You have ${session.getData(Constant.POINTS)} Points"

                btnPurchase.setOnClickListener {
                    val intent = Intent(activity, PurchasepointActivity::class.java)
                    activity.startActivity(intent)
                    dialogBuilder.dismiss()
                }


                btnFreePoints.setOnClickListener {
                    val intent = Intent(activity, SpinActivity::class.java)
                    startActivity(intent)

                }

                dialogBuilder.show()

                //    Toast.makeText(this, chat_status, Toast.LENGTH_SHORT).show()

            }





        })

        chatViewModel.deleteChatLiveData.observe(this, Observer
        {
            if (it.success) {
                Toast.makeText(
                    mContext, getString(R.string.str_error_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
                val Intent = Intent(activity, HomeActivity::class.java)
                startActivity(Intent)
                finish()

            } else {
                Toast.makeText(
                    mContext, getString(R.string.str_error_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        chatViewModel.otherUserDetailLiveData.observe(this, Observer
        {
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

        chatViewModel.readChatLiveData.observe(this, Observer
        {
            if (it.success) {

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.addFriendLiveData.observe(this, Observer
        {
            if (it.success) {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        reportviewModel.reportFrientLiveData.observe(this, Observer {
            if (it.success) {

                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
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

                    R.id.menu_remove -> {
                        viewModel.addFriend(
                            session.getData(Constant.USER_ID).toString(),
                            receiverId.toString(), "2"
                        )
                        true
                    }

                    R.id.menu_report -> {
                        showReportInputDialog()

                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun showReportInputDialog() {
        // Inflate custom dialog layout
        val dialogView =
            LayoutInflater.from(activity).inflate(R.layout.custom_report_dialog, null)

        // Find the views
        val etMessage: EditText = dialogView.findViewById(R.id.etMessage)
        val btnSubmit: Button = dialogView.findViewById(R.id.btnSubmit)
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)

        // Create AlertDialog with the custom layout
        val dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .create()

        // Set click listener for submit button
        btnSubmit.setOnClickListener {
            val etMessages = etMessage.text.toString().trim()

            // Validate the mobile number
            if (etMessages.isEmpty()) {
                Toast.makeText(activity, "Please enter Report Message", Toast.LENGTH_SHORT)
                    .show()
            }  else {
                reportviewModel.reportFriend(
                    session.getData(Constant.USER_ID).toString(),
                    receiverId,
                    etMessages
                )

                dialog.dismiss() // Dismiss the dialog after submitting
            }
        }

        // Set click listener for cancel button
        btnCancel.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog when cancelled
        }

        // Show the dialog
        dialog.show()
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

        if (isNetworkAvailable(mContext)) {
            senderChatReference.removeValue().addOnSuccessListener {
                chatViewModel.deleteChat(
                    session.getData(Constant.USER_ID).toString(),
                    receiverId
                )
                makeToast("Chat cleared successfully")
                messages.clear()
                chatAdapter?.notifyDataSetChanged()
            }.addOnFailureListener {
                makeToast("Failed to clear chat")
            }

        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
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
            onMessageDelete = { chatModel -> },
                    receiverName.toString(),

        )



        binding.RVChats.apply {
            layoutManager = LinearLayoutManager(this@ChatsActivity)
            adapter = chatAdapter
            scrollToPosition(chatAdapter!!.itemCount - 1)
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


    private fun loadRewardedVideoAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity, adId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                rewardedAd = null
                //  Toast.makeText(this@FreePointsActivity, "Ad Failed To Load", Toast.LENGTH_SHORT).show()
            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                //  Toast.makeText(this@FreePointsActivity, "Ad Loaded", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRewardedVideoAd() {
        rewardedAd?.let { ad ->
            ad.show(activity) { rewardItem: RewardItem ->

                val intent = Intent(activity, SpinActivity::class.java)
                activity.startActivity(intent)
            }
        } ?: run {
            loadRewardedVideoAd()
        }
    }

}

const val CHATS_ACTIVITY = "ChatsActivity"
