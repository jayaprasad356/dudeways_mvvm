package com.gmwapp.dudeways.application

import android.app.Application
import androidx.core.provider.FontRequest
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import androidx.work.Configuration
import com.gmwapp.dudeways.R
import com.google.firebase.FirebaseApp
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()
     //   initEmojiCompat()
        FirebaseApp.initializeApp(this)
        EmojiManager.install(GoogleEmojiProvider())



    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().setWorkerFactory(workerFactory).build()

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