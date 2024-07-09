package com.example.musicapp.selection

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivitySelectionBinding
import com.example.musicapp.home.HomeFragment
import com.example.musicapp.home.HomeFragment.Companion.musicList
import com.example.musicapp.home.HomeFragment.Companion.musicSearchList
import com.example.musicapp.home.HomeFragment.Companion.search
import com.example.musicapp.home.MusicAdapter
import com.example.musicapp.playlist.PlaylistDetailsFragment
import com.example.musicapp.playlist.PlaylistFragment

class SelectionActivity : AppCompatActivity() {

    lateinit var binding: ActivitySelectionBinding
    lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter= MusicAdapter(this, HomeFragment.musicList ,this,playListDetails = false,selectionActivity = true)
        binding.selectionSongRecyclerView.adapter= adapter


        binding.searchBar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                musicSearchList = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in musicList) {
                        if (song.title?.lowercase()?.contains(userInput) == true) {
                            musicSearchList.add(song)

                            search = true
                        }
                    }
                    (binding.selectionSongRecyclerView.adapter as MusicAdapter).updateMusicList(musicSearchList)

                }
                return true
            }
        })

        binding.backBtn.setOnClickListener {
            finish()
        }


    }
}