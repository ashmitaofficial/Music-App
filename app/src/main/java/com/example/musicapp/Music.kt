package com.example.musicapp

import kotlin.system.exitProcess

data class Music(
    val id: String?,
    val title: String?,
    val album: String?,
    val artist: String?,
    val duration: Long? = 0,
    val path: String?,
    val artUri: String?
)

fun setSongPosition(increment: Boolean) {
    if (!PlayerFragment.repeatBtn) {
        if (increment) {
            if (PlayerFragment.musicList.size - 1 == PlayerFragment.songPosition) {
                PlayerFragment.songPosition = 0
            } else {
                PlayerFragment.songPosition = PlayerFragment.songPosition!! + 1
            }
        } else {
            if (0 == PlayerFragment.songPosition) {
                PlayerFragment.songPosition = PlayerFragment.musicList.size - 1
            } else {
                PlayerFragment.songPosition = PlayerFragment.songPosition!! - 1
            }
        }
    }
}

fun exitApplication() {
    if (PlayerFragment.musicService != null) {
        PlayerFragment.musicService?.stopForeground(true)
        PlayerFragment.musicService?.mediaPlayer?.release()
        PlayerFragment.musicService = null
    }
    exitProcess(1)

}