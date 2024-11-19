package com.gmwapp.dudeways.activity

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityCallBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.CallViewModel
import com.zegocloud.uikit.ZegoUIKit
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallType
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallUser
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoInvitationCallListener
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import dagger.hilt.android.AndroidEntryPoint
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.Timer
import java.util.TimerTask


@AndroidEntryPoint
class CallActivity : BaseActivity() {
    lateinit var binding: ActivityCallBinding
    lateinit var mContext: CallActivity
    lateinit var activity: Activity
    lateinit var session: Session
    private val viewModel: CallViewModel by viewModels()

    // Declare the TextView for displaying the call duration
    private lateinit var textView: TextView
    private var roomID: String? = null
    private var duration = 0
    private var timer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())


    private var userId: String = ""
    private var callUserId: String = ""
    private var startTime: String = ""
    private var endTime: String = ""
    var targetUserId: String? = null

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").apply {
        timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Set to IST time zone
    }



    private lateinit var voiceCallButton: ZegoSendCallInvitationButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call)
        mContext = this
        initUI()
        addListner()
        addObsereves()


        voiceCallButton = findViewById(R.id.voiceCallButton)

       addRoomStateChangedListener()

    }



    private fun addRoomStateChangedListener() {
        ZegoUIKitPrebuiltCallService.events.invitationEvents.invitationListener =
            object : ZegoInvitationCallListener {
                override fun onIncomingCallReceived(
                    callID: String?,
                    caller: ZegoCallUser?,
                    callType: ZegoCallType?,
                    callees: MutableList<ZegoCallUser>?
                ) {
                }

                override fun onIncomingCallCanceled(callID: String?, caller: ZegoCallUser?) {
                    Toast.makeText(mContext,"Call Cancelled",Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onIncomingCallTimeout(callID: String?, caller: ZegoCallUser?) {
                    Toast.makeText(mContext,"Call Timeout",Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onOutgoingCallAccepted(callID: String?, callee: ZegoCallUser?) {
                }


                override fun onOutgoingCallRejectedCauseBusy(
                    callID: String?,
                    callee: ZegoCallUser?
                ) {
                    Toast.makeText(mContext,"Call Rejected",Toast.LENGTH_SHORT).show()
                }

                override fun onOutgoingCallDeclined(callID: String?, callee: ZegoCallUser?) {
                   // addObsereves()
                    Toast.makeText(mContext,"Call Rejected",Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onOutgoingCallTimeout(
                    callID: String?,
                    callees: MutableList<ZegoCallUser>?
                ) {
                    Toast.makeText(mContext,"Call Timeout",Toast.LENGTH_SHORT).show()
                    TODO("Not yet implemented")
                }
            }


        ZegoUIKit.addRoomStateChangedListener { room, reason, _, _ ->
            when (reason) {
                ZegoRoomStateChangedReason.LOGINED -> {
                    roomID = room
                    userId = session.getData(Constant.USER_ID).toString() // Set user_id
                    callUserId = targetUserId.toString() // Set call_user_id
                    startTime = dateFormat.format(Date()) // Set call start time in IST
                   addTextView()   // Add TextView to display duration
                    startTimer()    // Start the timer when call start
                }
                ZegoRoomStateChangedReason.LOGOUT ->{
                    stopTimer()
                    endTime = dateFormat.format(Date()) // Set call end time in IST
                    logCallDetails() // Log call details including duration
             //       addObsereves()
                    binding.rl2.visibility = View.VISIBLE
                    binding.rl1.visibility = View.GONE

                }



                else -> { /* Handle other cases if necessary */ }
            }
        }




    }



    private fun logCallDetails() {
        val durationInSec = duration
        // Log or save user_id, call_user_id, start_time, end_time, and duration here
        println("User ID: $userId")
        println("Call User ID: $callUserId")
        println("Start Time: $startTime")
        println("End Time: $endTime")
        println("Duration: $durationInSec seconds")
//        binding.tvText6.text= "Last Call : $duration"
      //  Toast.makeText(this, "Duration: ${transToHourMinSec(durationInSec)}", Toast.LENGTH_SHORT).show()
//
//        binding.tvText3.text = "User ID: $userId\n" +
//                "Call User ID: $callUserId\n" +
//                "Start Time: $startTime\n" +
//                "End Time: $endTime\n" +
//                "Duration: ${transToHourMinSec(duration)}"

        usercall(userId,callUserId,startTime,endTime,duration)
    }



    private fun addTextView() {
//        val rootView = binding.root as RelativeLayout
//        textView = TextView(this).apply {
//            setTextColor(Color.WHITE)
//            textSize = 18f
//        }
//        val params = RelativeLayout.LayoutParams(
//            RelativeLayout.LayoutParams.WRAP_CONTENT,
//            RelativeLayout.LayoutParams.WRAP_CONTENT
//        ).apply {
//            addRule(RelativeLayout.CENTER_HORIZONTAL)
//            addRule(RelativeLayout.ALIGN_PARENT_TOP)
//            topMargin = (20 * resources.displayMetrics.density).toInt()
//        }
//        rootView.addView(textView, params)
    }

    private fun startTimer() {
        duration = 0
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                duration++
                handler.post { binding.tvText6.text = transToHourMinSec(duration) }
            }
        }, 0, 1000)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    private fun transToHourMinSec(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }



    private fun addObsereves() {
        viewModel.callLiveData.observe(this) { response ->
            if (response.success) {
                val callType = intent.getStringExtra("call_type")
                val  call_user_id = intent.getStringExtra("call_user_id")
                 targetUserId = when (callType) {
                    "random_call" -> response.data.unique_name.toString()
                    "chat_call" -> call_user_id.toString()
                     "test_call" ->  call_user_id.toString()
                    else -> null
                }

             //  Toast.makeText(this, targetUserId, Toast.LENGTH_SHORT).show()

                if (targetUserId != null) {
                    val targetName = response.data.name.toString()
                    val type = intent.getStringExtra("type")

                    when (type) {
                        "audio" -> StartVoiceCall(targetUserId!!, targetName)
                        "video" -> StartVideoCall(targetUserId!!, targetName)
                        else -> Toast.makeText(this, "Invalid call type", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Target user ID not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            }
        }

        // Uncomment if you want to observe `usercallLiveData`
        // viewModel.usercallLiveData.observe(this) { response ->
        //     Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
        // }
    }

    private fun addListner() {
        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener{
            addObsereves()
        }

        binding.btnBack.setOnClickListener{
            finish()
        }
    }



    private fun initUI() {
        activity = this
        session = Session(activity)


        if (isNetworkAvailable(mContext)) {
               viewModel.getRandomUser(session.getData(Constant.USER_ID).toString())
           } else {
               Toast.makeText(
                   mContext, getString(R.string.str_error_internet_connections),
                   Toast.LENGTH_SHORT
               ).show()
           }




    }

    private fun usercall(
        userId: String,
        callUserId: String,
        startTime: String,
        endTime: String,
        duration: Int
    ) {
        if (isNetworkAvailable(mContext)) {
            viewModel.setUserCall(userId,callUserId,startTime,endTime,duration)
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun StartVoiceCall(targetUserId: String, targetName: String) {
        voiceCallButton.setIsVideoCall(false)
        voiceCallButton.resourceID = "zego_uikit_call"
        voiceCallButton.setInvitees(listOf(ZegoUIKitUser(targetUserId, targetName)))


        lifecycleScope.launch {
            delay(4000)  // 4-second delay before initiating the call
            voiceCallButton.performClick() // Programmatically click to start the call

            // Additional delay if needed to allow time for the call to start
            delay(4000)  // Additional delay to allow the call setup
        }


    }

    private fun StartVideoCall(targetUserId: String, targetName: String) {
        voiceCallButton.setIsVideoCall(true)
        voiceCallButton.resourceID = "zego_uikit_call"
        voiceCallButton.setInvitees(listOf(ZegoUIKitUser(targetUserId, targetName)))
        lifecycleScope.launch {
            delay(4000)  // 4-second delay before initiating the call
            voiceCallButton.performClick() // Programmatically click to start the call

            // Additional delay if needed to allow time for the call to start
            delay(4000)  // Additional delay to allow the call setup
             // Finish the activity after call initiation
        }
    }

}
