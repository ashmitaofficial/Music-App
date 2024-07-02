package com.example.musicapp

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.musicapp.databinding.FragmentPlayerBinding
import com.example.musicapp.home.HomeFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false

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
                val equiIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                equiIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService?.mediaPlayer?.audioSessionId
                )
                equiIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context?.packageName)
                equiIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(equiIntent, 13)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Equalizer not supported", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding?.timerBtn?.setOnClickListener {
            val timer = min15 || min30 || min60
            if (timer == false) {
                showTimerBottomSheet()
            } else {
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to stop timer?")
                    .setPositiveButton("Yes") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding?.timerBtn?.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.cool_pink
                            ))
                    }
                    .setNegativeButton("No"){dialog,_ -> dialog.dismiss() }
                val customDialog= builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }

        binding?.playPauseBtn?.setOnClickListener {
            if (isPlaying) {
                pauseMusic()

            } else {
                playMusic()

            }
        }


        binding?.seekBarPA?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })

        binding?.repeatBtn?.setOnClickListener {
            if (!repeatBtn) {
                repeatBtn = true
                binding?.repeatBtn?.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple
                    )
                )
            } else {
                repeatBtn = false
                binding?.repeatBtn?.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.cool_pink
                    )
                )
            }
        }

        binding?.shareBtn?.setOnClickListener {
            val shareIntent= Intent()
            //set action means ye action batayega ki ye intent krta kya h
            shareIntent.action= Intent.ACTION_SEND
            //kis type ki intent share ho rha h == * se phle type of file batate h agar video hoti ti video/* likhte
            shareIntent.type="audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM,  Uri.parse(musicList[songPosition!!].path))
            startActivity(Intent.createChooser(shareIntent,"Sharing Music File"))
        }

        when (arguments?.getString("class")) {

            "MusicAdapterSearch" ->{
                musicList = ArrayList()
                musicList.addAll(HomeFragment.musicSearchList)
                setLayout()
            }



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
        if (repeatBtn == true) {
            binding?.repeatBtn?.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple
                )
            )
        }
        if (min15 || min30 || min60) {
            binding?.timerBtn?.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple
                )
            )
        }else{

        }

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
            binding?.startTimingPA?.text = musicService?.mediaPlayer?.currentPosition?.toLong()
                ?.let { convertDuration(it) }
            binding?.endTimingPA?.text = musicService?.mediaPlayer?.duration?.toLong()
                ?.let { convertDuration(it) }
            binding?.seekBarPA?.progress = 0
            binding?.seekBarPA?.max = musicService?.mediaPlayer?.duration!!
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

    fun showTimerBottomSheet() {
        val timerBottomsheet = BottomSheetDialog(requireContext())
        timerBottomsheet.setContentView(R.layout.timer_bottomsheet)
        timerBottomsheet.show()
        timerBottomsheet.findViewById<TextView>(R.id.fifteenMinTimer)?.setOnClickListener {
            Toast.makeText(requireContext(), "Music will stop after 15 minutes", Toast.LENGTH_SHORT)
                .show()
            min15=true
            Thread {
                Thread.sleep(15 * 60000)
                if (min15)
                    exitApplication()
            }.start()
            timerBottomsheet.dismiss()
            binding?.timerBtn?.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple
                ))
        }
        timerBottomsheet.findViewById<TextView>(R.id.thirtyMinTimer)?.setOnClickListener {
            Toast.makeText(requireContext(), "Music will stop after 30 minutes", Toast.LENGTH_SHORT)
                .show()
            min30 = true
            Thread {
                Thread.sleep(30 * 60000)
                if (min30)
                    exitApplication()
            }.start()
            binding?.timerBtn?.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple
                )
            )


            timerBottomsheet.dismiss()
        }
        timerBottomsheet.findViewById<TextView>(R.id.sixtyMinTimer)?.setOnClickListener {
            Toast.makeText(requireContext(), "Music will stop after 60 minutes", Toast.LENGTH_SHORT)
                .show()
            min60 = true
            Thread {
                Thread.sleep(60 * 60000)
                if (min60)
                    exitApplication()
            }.start()
            timerBottomsheet.dismiss()
            binding?.timerBtn?.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple
                ))
        }
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
        if (requestCode == 13 || resultCode == RESULT_OK) {
            return
        }
    }

}