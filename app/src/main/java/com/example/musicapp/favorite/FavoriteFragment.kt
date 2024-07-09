package com.example.musicapp.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.Music
import com.example.musicapp.PlayerFragment
import com.example.musicapp.R
import com.example.musicapp.checkPlaylist
import com.example.musicapp.databinding.FragmentFavoriteBinding
import com.example.musicapp.databinding.FragmentPlayerBinding
import com.example.musicapp.home.HomeFragment.Companion.musicList
import com.example.musicapp.home.MusicAdapter

class FavoriteFragment : Fragment() {

    lateinit var binding: FragmentFavoriteBinding

    companion object{
        var favoriteSongs: ArrayList<Music> = ArrayList()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        favoriteSongs= checkPlaylist(favoriteSongs)

        binding.favSongRecyclerView.adapter= FavoriteAdapter(requireActivity(), favoriteSongs)

        if(favoriteSongs.size<1){
            binding.shuffleBtnFA.visibility= View.GONE
        }
        binding.shuffleBtnFA.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("index", 0)
            bundle.putString("class", "FavoriteShuffle")
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, PlayerFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }


        return binding.root
    }


}