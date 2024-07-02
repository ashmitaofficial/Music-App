package com.example.musicapp.favorite

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.databinding.FavSongItemBinding
import com.example.musicapp.databinding.FragmentFavoriteBinding

class FavoriteAdapter(
    private val context: Context,
    private var musicList: ArrayList<String>,
    val activity: FragmentActivity
) : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteAdapter.MyViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.MyViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class MyViewHolder(binding: FavSongItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val songPic = binding.favSongPic
        val songName = binding.favSongName
    }
}