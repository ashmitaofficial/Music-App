package com.example.musicapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> {
                prevNextSong(false, context!!)

            }


            ApplicationClass.PLAY -> {
                if (PlayerFragment.isPlaying) {
                    pauseMusic()
                } else {
                    playMusic()
                }
            }

            ApplicationClass.NEXT -> {
                prevNextSong(true, context!!)
            }


            ApplicationClass.EXIT -> {
                //stopForeground true means notification close
                exitApplication()
            }
        }
    }

    private fun playMusic() {
        PlayerFragment.isPlaying = true
        PlayerFragment.musicService?.mediaPlayer?.start()
        PlayerFragment.musicService?.showNotification(R.drawable.pause_icon)
        PlayerFragment.binding?.playPauseBtn?.setIconResource(R.drawable.pause_icon)
    }

    private fun pauseMusic() {
        PlayerFragment.isPlaying = false
        PlayerFragment.musicService?.mediaPlayer?.pause()
        PlayerFragment.musicService?.showNotification(R.drawable.play_icon)
        PlayerFragment.binding?.playPauseBtn?.setIconResource(R.drawable.play_icon)
    }

    private fun prevNextSong(increment: Boolean, context: Context?) {
        setSongPosition(increment = increment)
        PlayerFragment.musicService?.createMediaPlayer()
//        Picasso.get().load(PlayerFragment.musicList[PlayerFragment.songPosition!!].album)
//            .placeholder(R.drawable.song_pic)
//            .into(PlayerFragment.binding?.songPic)
        PlayerFragment.binding?.songName?.text =
            PlayerFragment.musicList[PlayerFragment.songPosition!!].title
        playMusic()

    }
}