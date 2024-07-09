package com.example.musicapp.playlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp.AppConstants
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityPlaylistDetailsBinding
import com.example.musicapp.home.HomeFragment
import com.example.musicapp.home.MusicAdapter
import com.squareup.picasso.Picasso

class PlaylistDetailsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_playlist_details)

        val pos = intent?.getIntExtra(AppConstants.PLAYLIST_POSITION, -1)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = Bundle()
        pos?.let {
            bundle.putInt("index", it)
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.container, PlaylistDetailsFragment::class.java, bundle)
            .commit()

    }



}