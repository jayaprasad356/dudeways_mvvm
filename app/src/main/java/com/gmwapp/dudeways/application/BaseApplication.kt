package com.gmwapp.dudeways.application

import android.app.Application
import androidx.core.provider.FontRequest
import com.gmwapp.dudeways.R
import com.google.firebase.FirebaseApp
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
     //   initEmojiCompat()
        FirebaseApp.initializeApp(this)
        EmojiManager.install(GoogleEmojiProvider())
    }

//    private fun initEmojiCompat() {
//        val fontRequest = FontRequest(
//            "com.google.android.gms.fonts",
//            "com.google.android.gms",
//            "Noto Color Emoji Compat",
//            R.array.com_google_android_gms_fonts_certs
//        )
//
//    }
}