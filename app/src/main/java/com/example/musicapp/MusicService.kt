package com.example.musicapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MusicService: Service() {

    private var myBinder= MyBinder()
    var mediaPlayer:MediaPlayer?=null

//    //this methods calls when we bind any service to the activity
    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    inner class MyBinder: Binder()
    {
        fun currentService():MusicService{
            return this@MusicService
        }
    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startForeground(1, createNotification())
//        mediaPlayer = MediaPlayer.create(this, R.raw.abc)
//        mediaPlayer!!.isLooping = false
//        mediaPlayer!!.start()
//        return START_STICKY
//    }
//
//    private fun createNotification(): Notification {
//        val intent = Intent(this, MusicService::class.java)
//        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        prepareChannel(this, "1", NotificationManagerCompat.IMPORTANCE_LOW)
//        val builder = NotificationCompat.Builder(this, "1")
//            .setContentTitle("Music App")
//            .setContentText("My music app is turned on")
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .addAction(R.drawable.ic_launcher_background, "stop", pendingIntent)
//        return builder.build()
//    }
//
//    private fun prepareChannel(context: Context, id: String, importance: Int) {
//        val nm = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        if (nm != null) {
//            var nChannel = nm.getNotificationChannel(id)
//            if (nChannel == null) {
//                nChannel = NotificationChannel(id, "appName", importance)
//                nChannel.description = "description"
//                nm.createNotificationChannel(nChannel)
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        mediaPlayer!!.stop()
//        super.onDestroy()
//    }




}