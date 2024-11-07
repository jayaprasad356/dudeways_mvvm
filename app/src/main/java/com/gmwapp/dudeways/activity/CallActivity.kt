package com.gmwapp.dudeways.activity

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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





    private lateinit var voiceCallButton: ZegoSendCallInvitationButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call)
        mContext = this
        initUI()
        addListner()
        addObsereves()

        binding.targetuserid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if(binding.targetuserid.text.isNotEmpty()){
                    addObsereves()

                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

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
                }

                override fun onIncomingCallTimeout(callID: String?, caller: ZegoCallUser?) {
                }

                override fun onOutgoingCallAccepted(callID: String?, callee: ZegoCallUser?) {
                }

                override fun onOutgoingCallRejectedCauseBusy(
                    callID: String?,
                    callee: ZegoCallUser?
                ) {
                }

                override fun onOutgoingCallDeclined(callID: String?, callee: ZegoCallUser?) {
                    addObsereves()
                }

                override fun onOutgoingCallTimeout(
                    callID: String?,
                    callees: MutableList<ZegoCallUser>?
                ) {
                    TODO("Not yet implemented")
                }
            }


        ZegoUIKit.addRoomStateChangedListener { room, reason, _, _ ->
            when (reason) {
                ZegoRoomStateChangedReason.LOGINED -> {
                    roomID = room
                    addTextView()   // Add the TextView to display duration
                    startTimer()    // Start the timer when call starts
                }
                ZegoRoomStateChangedReason.LOGOUT ->{
                    stopTimer()
                    addObsereves()
                }

                else -> { /* Handle other cases if necessary */ }
            }
        }




    }

    private fun addTextView() {
        // Access the root view of the layout
        val rootView = binding.root as RelativeLayout
        textView = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 18f
        }

        // Layout parameters for positioning the TextView centered horizontally at the top
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.CENTER_HORIZONTAL)
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            topMargin = (20 * resources.displayMetrics.density).toInt()
        }

        rootView.addView(textView, params)
    }


    private fun startTimer() {
        duration = 0
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                duration++
                handler.post {
                    textView.text = transToHourMinSec(duration) // Update the duration display
                }
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
        viewModel.callLiveData.observe(this, Observer {
            if (it.success){
                //val targetUserId = it.data.unique_name.toString()


                val targetUserId = binding.targetuserid.text.toString()
                val targetName = it.data.name.toString()

                val type = intent.getStringExtra("type")

                if (type == "audio") {
                    StartVoiceCall(targetUserId,targetName)
                }
                else if (type == "video") {
                    StartVideoCall(targetUserId,targetName)
                }

            }else{
                Toast.makeText(mContext,it.message,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addListner() {
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }



    private fun initUI() {
        activity = this
        session = Session(activity)
//
//        binding.ivBack.setOnClickListener {
//
//            onBackPressed()
//
//        }




        if (isNetworkAvailable(mContext)) {
            viewModel.getRandomUser(session.getData(Constant.USER_ID).toString())
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

//        lifecycleScope.launch {
//            delay(4000)  // 4-second delay before initiating the call
//            voiceCallButton.performClick() // Programmatically click to start the call
//
//            // Additional delay if needed to allow time for the call to start
//            delay(4000)  // Additional delay to allow the call setup
//        }


    }

    private fun StartVideoCall(targetUserId: String, targetName: String) {
        voiceCallButton.setIsVideoCall(true)
        voiceCallButton.resourceID = "zego_uikit_call"
        voiceCallButton.setInvitees(listOf(ZegoUIKitUser(targetUserId, targetName)))
//        lifecycleScope.launch {
//            delay(4000)  // 4-second delay before initiating the call
//            voiceCallButton.performClick() // Programmatically click to start the call
//
//            // Additional delay if needed to allow time for the call to start
//            delay(4000)  // Additional delay to allow the call setup
//             // Finish the activity after call initiation
//        }
    }

}
