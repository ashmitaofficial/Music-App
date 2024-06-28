package com.example.musicapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass : Application() {
    companion object {
        const val CHANNEL_ID = "channel1"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
    }

    override fun onCreate() {
        super.onCreate()
//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)

        val notificationChannel =
            NotificationChannel(CHANNEL_ID, "now playing song", NotificationManager.IMPORTANCE_HIGH)
        //user can read the description
        notificationChannel.description = "This is a channel for showing songs"
        //now we have to create notification manager so that it can create a notification channel at runtime
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)


    }
}