package com.example.musicapp

import com.example.musicapp.favorite.FavoriteFragment
import com.example.musicapp.playlist.PlaylistFragment
import java.io.File
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

class Playlist {
    lateinit var name: String
    lateinit var playlist: ArrayList<Music>
    lateinit var createdBy: String
    lateinit var createdOn: String
}

class MusicPlaylist {
    var ref: ArrayList<Playlist> = ArrayList()
}

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

fun favoriteChecker(id: String): Int {
    PlayerFragment.isFavorite = false
    FavoriteFragment.favoriteSongs.forEachIndexed { index, music ->
        if (id == music.id) {
            PlayerFragment.isFavorite = true
            return index
        }
    }
    return -1
}

fun checkPlaylist(playlist: ArrayList<Music>):ArrayList<Music>{

    val updatedList= ArrayList<Music>()

    playlist.forEachIndexed { index, music ->
        val file= music.path?.let { File(it) }
        if (file != null) {
            if(file.exists()){
                updatedList.add(playlist[index])
            }
        }
    }


    return updatedList
}