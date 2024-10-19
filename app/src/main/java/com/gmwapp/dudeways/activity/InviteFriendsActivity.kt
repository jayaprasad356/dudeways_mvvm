package com.gmwapp.dudeways.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityInviteFriendsBinding
import com.gmwapp.dudeways.utils.Session
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InviteFriendsActivity : AppCompatActivity() {

    lateinit var binding: ActivityInviteFriendsBinding
    lateinit var mContext: InviteFriendsActivity
    lateinit var session: Session

    var baseUrl: String = "https://play.google.com/store/apps/details?id=com.gmwapp.dudeways"
    val referralCode = "aShgEc"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invite_friends)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        session = Session(mContext)

        binding.btnReferText.text = referralCode

    }

    private fun addListner() {
        binding.btnRefer.setOnClickListener {
            val urlWithReferral = generateReferralUrl(baseUrl, referralCode)
            shareTextAndUrl("Click this link to join Dude Ways App ☺", urlWithReferral)
        }

        binding.btnReferText.setOnClickListener {
            copyTextToClipboard(referralCode)
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Refer Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "sent to device", Toast.LENGTH_SHORT).show()
    }

    private fun generateReferralUrl(baseUrl: String, referralCode: String): String {
        return "$baseUrl&referralCode=$referralCode"
    }

    private fun shareTextAndUrl(text: String, url: String) {
        val shareContent = "$text\n$url"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareContent)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

}