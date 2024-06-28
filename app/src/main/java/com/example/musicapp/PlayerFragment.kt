package com.example.musicapp

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.musicapp.databinding.FragmentPlayerBinding
import com.squareup.picasso.Picasso


class PlayerFragment : Fragment(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var musicList: ArrayList<Music>
        var songPosition: Int? = 0

        //      var mediaPlayer: MediaPlayer? = null
        var musicService: MusicService? = null
        var isPlaying: Boolean = false
        var repeatBtn: Boolean = false
        lateinit var backBtn: Button

        @SuppressLint("StaticFieldLeak")
        var binding: FragmentPlayerBinding? = null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlayerBinding.inflate(inflater, container, false)

        songPosition = arguments?.getInt("index", 0)


        //For starting service
        val myIntent = Intent(requireContext(), MusicService::class.java)
        requireActivity().bindService(myIntent, this, BIND_AUTO_CREATE)
        requireActivity().startService(myIntent)


        binding?.prevBtn?.setOnClickListener {
            prevNextSong(increment = false)
        }

        binding?.nextBtn?.setOnClickListener {
            prevNextSong(increment = true)
        }

        binding?.backBtn?.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding?.equilizerBtn?.setOnClickListener {
            try {
                val equiIntent= Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                equiIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService?.mediaPlayer?.audioSessionId)
                equiIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,context?.packageName)
                equiIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC )
                startActivityForResult(equiIntent,13)
            }catch (e:Exception){
                Toast.makeText(requireContext(),"Equalizer not supported",Toast.LENGTH_SHORT).show()
            }
        }

        binding?.playPauseBtn?.setOnClickListener {
            if (isPlaying) {
                pauseMusic()

            } else {
                playMusic()

            }
        }


        binding?.seekBarPA?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    musicService?.mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)= Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })

        binding?.repeatBtn?.setOnClickListener {
            if (!repeatBtn)
            {
                repeatBtn=true
                binding?.repeatBtn?.setColorFilter(ContextCompat.getColor(requireContext(),R.color.purple))
            }else{
                repeatBtn=false
                binding?.repeatBtn?.setColorFilter(ContextCompat.getColor(requireContext(),R.color.cool_pink))
            }
        }




        when (arguments?.getString("class")) {
            "MusicAdapter" -> {
                musicList = ArrayList()
                musicList.addAll(HomeFragment.musicList)
                setLayout()
//                createMediaPlayer()
                isPlaying = true

            }

            "HomeFragment" -> {
                musicList = ArrayList()
                musicList.addAll(HomeFragment.musicList)
                musicList.shuffle()
//                createMediaPlayer()
                setLayout()
            }
        }
        return binding?.root
    }

    private fun setLayout() {
//        Picasso.get().load(musicList[songPosition!!].album).into(song_pic)
        Picasso.get().load(musicList[songPosition!!].album).placeholder(R.drawable.song_pic)
            .into(binding?.songPic)
        binding?.songName?.text = musicList[songPosition!!].title
        if(repeatBtn==true){binding?.repeatBtn?.setColorFilter(ContextCompat.getColor(requireContext(),R.color.purple))}

    }

    fun createMediaPlayer() {
        try {
            if (musicService?.mediaPlayer == null) {
                musicService?.mediaPlayer = MediaPlayer()
            }
            musicService?.mediaPlayer?.reset()
            musicService?.mediaPlayer?.setDataSource(musicList[songPosition!!].path)
            musicService?.mediaPlayer?.prepare()
            musicService?.mediaPlayer?.start()
            isPlaying = true
            binding?.playPauseBtn?.setIconResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon)
            binding?.startTimingPA?.text= musicService?.mediaPlayer?.currentPosition?.toLong()
                ?.let { convertDuration(it) }
            binding?.endTimingPA?.text= musicService?.mediaPlayer?.duration?.toLong()
                ?.let { convertDuration(it) }
            binding?.seekBarPA?.progress=0
            binding?.seekBarPA?.max= musicService?.mediaPlayer?.duration!!
            musicService?.mediaPlayer?.setOnCompletionListener(this)
        } catch (e: Exception) {
            return
        }
    }

    private fun playMusic() {
        musicService?.showNotification(R.drawable.pause_icon)
        binding?.playPauseBtn?.setIconResource(R.drawable.pause_icon)
        isPlaying = true
        musicService?.mediaPlayer?.let {
            it.start()
        }
    }

    private fun pauseMusic() {
        musicService?.showNotification(R.drawable.play_icon)
        binding?.playPauseBtn?.setIconResource(R.drawable.play_icon)
        isPlaying = false
        musicService?.mediaPlayer?.let {
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

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
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

    //when song completed this method is called...
    override fun onCompletion(mp: MediaPlayer?) {
        //ye fun song ki ek position ko increment krdega
        setSongPosition(true)
        //uske baad firse new media player create ho k chal jaye
        createMediaPlayer()
        setLayout()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==13 || resultCode== RESULT_OK){
            return
        }
    }

}