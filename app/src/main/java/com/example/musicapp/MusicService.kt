package com.example.musicapp

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.musicapp.PlayerFragment.Companion.musicService

class MusicService : Service() {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    lateinit var mediaSession: MediaSessionCompat
    lateinit var runnable: Runnable

    //    this methods calls when we bind any service to the activity
    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }


    fun showNotification(playPauseBtn: Int) {

        val prevIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerFragment.musicList[PlayerFragment.songPosition!!].title)
            .setContentText(PlayerFragment.musicList[PlayerFragment.songPosition!!].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.song_pic))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.back_icon, "previous", prevPendingIntent)
            .addAction(playPauseBtn, "play", playPendingIntent)
            .addAction(R.drawable.next_notification_icon, "next", nextPendingIntent)
            .addAction(R.drawable.exit_icon, "exit", exitPendingIntent)
            .build()

        startForeground(13, notification)
    }

    fun createMediaPlayer() {
        try {
            if (musicService?.mediaPlayer == null) {
                musicService?.mediaPlayer = MediaPlayer()
            }
            musicService?.mediaPlayer?.reset()
            musicService?.mediaPlayer?.setDataSource(PlayerFragment.musicList[PlayerFragment.songPosition!!].path)
            musicService?.mediaPlayer?.prepare()
            PlayerFragment.binding?.startTimingPA?.text =
                musicService?.mediaPlayer?.currentPosition?.toLong()
                    ?.let { convertDuration(it) }.toString()
            PlayerFragment.binding?.endTimingPA?.text =
                musicService?.mediaPlayer?.duration?.toLong()
                    ?.let { convertDuration(it) }.toString()
            PlayerFragment.binding?.seekBarPA?.progress = 0
            PlayerFragment.binding?.seekBarPA?.max = musicService?.mediaPlayer?.duration!!
        } catch (e: Exception) {
            return
        }
    }


    fun convertDuration(duration: Long): String? {
        var out: String? = null
        var hours: Long = 0
        hours = try {
            duration / 3600000
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return out
        }
        val remaining_minutes = (duration - hours * 3600000) / 60000
        var minutes = remaining_minutes.toString()
        if (minutes.equals(0)) {
            minutes = "00"
        }
        val remaining_seconds = duration - hours * 3600000 - remaining_minutes * 60000
        var seconds = remaining_seconds.toString()
        seconds = if (seconds.length < 2) {
            "00"
        } else {
            seconds.substring(0, 2)
        }
        out = if (hours > 0) {
            "$hours:$minutes:$seconds"
        } else {
            "$minutes:$seconds"
        }
        return out
    }

    fun seekBarSetup() {
        runnable= Runnable {
            PlayerFragment.binding?.startTimingPA?.text = musicService?.mediaPlayer?.currentPosition?.toLong()?.let { convertDuration(it) }.toString()
            PlayerFragment.binding?.seekBarPA?.progress = mediaPlayer?.currentPosition!!
            //Handler batata h ki ye jo code h kitne time baad run hona chahiye runnable se attach h handler
                Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }
}
