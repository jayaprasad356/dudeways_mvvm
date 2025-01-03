package com.gmwapp.dudeways

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SmsBroadcastReceiver : BroadcastReceiver() {

    var smsBroadcastReceiverListener: SmsBroadcastReceiverListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras: Bundle? = intent.extras
            val smsRetrieverStatus: Status? = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status

            when (smsRetrieverStatus?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val messageIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    smsBroadcastReceiverListener?.onSuccess(messageIntent)
                }
                CommonStatusCodes.TIMEOUT -> {
                    smsBroadcastReceiverListener?.onFailure()
                }
            }
        }
    }

    interface SmsBroadcastReceiverListener {
        fun onSuccess(intent: Intent?)
        fun onFailure()
    }
}