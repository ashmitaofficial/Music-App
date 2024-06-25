package com.example.musicapp

import android.content.ComponentName
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso


class PlayerFragment : Fragment() {

    companion object {
        lateinit var musicList: ArrayList<Music>
        var songPosition: Int? = 0
        var mediaPlayer: MediaPlayer? = null
        var musicService: MusicService? = null

    }

    lateinit var song_pic: ImageView
    lateinit var pauseBtn: Button
    lateinit var playBtn: Button
    lateinit var prevBtn: Button
    lateinit var nextBtn: Button
    lateinit var songName: TextView
    var isPlaying: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        pauseBtn = view.findViewById(R.id.pauseBtn)
        song_pic = view.findViewById(R.id.song_pic)
        songName = view.findViewById(R.id.song_name)
        playBtn = view.findViewById(R.id.playBtn)
        prevBtn = view.findViewById(R.id.prevBtn)
        nextBtn = view.findViewById(R.id.nextBtn)

        //For starting service


        prevBtn.setOnClickListener {
            prevNextSong(increment = false)
        }

        nextBtn.setOnClickListener {
            prevNextSong(increment = true)
        }


        pauseBtn.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
                playBtn.visibility = View.VISIBLE
                pauseBtn.visibility = View.GONE
            }
        }

        playBtn.setOnClickListener {
            if (isPlaying == false) {
                playMusic()
                playBtn.visibility = View.GONE
                pauseBtn.visibility = View.VISIBLE
            }
        }


        songPosition = arguments?.getInt("index", 0)

        when (arguments?.getString("class"))
        {
            "MusicAdapter" -> {
                musicList = ArrayList()
                musicList.addAll(HomeFragment.musicList)
                setLayout()
                createMediaPlayer()
                isPlaying = true

            }
            "HomeFragment"-> {
                musicList = ArrayList()
                musicList.addAll(HomeFragment.musicList)
                musicList.shuffle()
                createMediaPlayer()
                setLayout()
            }
        }
        return view.rootView
    }

    private fun setLayout() {
//        Picasso.get().load(musicList[songPosition!!].album).into(song_pic)
        Picasso.get().load(musicList[songPosition!!].album).placeholder(R.drawable.song_pic).into(song_pic)
        songName.text = musicList[songPosition!!].title

    }

     fun createMediaPlayer() {
        try {
            if (mediaPlayer == null) {
               mediaPlayer = MediaPlayer()
            }
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(musicList[songPosition!!].path)
           mediaPlayer?.prepare()
            mediaPlayer?.start()

        } catch (e: Exception) {
            return
        }
    }

    private fun playMusic() {
        isPlaying = true
        mediaPlayer?.let {
            it.start()
        }
    }

    private fun pauseMusic() {
        isPlaying = false
        mediaPlayer?.let {
            it.pause()
        }
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment == true) {
            setSongPosition(increment == true)
            setLayout()
            createMediaPlayer()
        } else {

            setSongPosition(increment == false)
            setLayout()
            createMediaPlayer()
        }

    }

    private fun setSongPosition(increment: Boolean) {
        if (increment) {
            if (musicList.size - 1 == songPosition) {
                songPosition = 0
            } else {
                songPosition = songPosition!! + 1
            }
        } else {
            if (0 == songPosition) {
                songPosition = musicList.size - 1
            } else {
                songPosition = songPosition!! - 1
            }
        }
    }

//    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//        val binder = service as MusicService.MyBinder
//        musicService= binder.currentService()
////        createMediaPlayer()
//
//    }
//
//    override fun onServiceDisconnected(name: ComponentName?) {
//        musicService= null
//    }

}