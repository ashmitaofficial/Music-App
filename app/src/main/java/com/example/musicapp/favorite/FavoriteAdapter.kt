package com.example.musicapp.favorite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.Music
import com.example.musicapp.PlayerFragment
import com.example.musicapp.R
import com.example.musicapp.databinding.FavSongItemBinding
import com.squareup.picasso.Picasso

class FavoriteAdapter(
    private val context: FragmentActivity,
    private var musicList: ArrayList<Music>,
) : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteAdapter.MyViewHolder {
        return MyViewHolder(FavSongItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.MyViewHolder, position: Int) {
        holder.songName.text = musicList[position].title
        Picasso.get().load(musicList[position].album).placeholder(R.drawable.song_pic)
            .into(holder.songPic)
        holder.cardView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("index", position)
            bundle.putString("class", "FavoriteAdapter")
            context.supportFragmentManager.beginTransaction()
                .add(R.id.container, PlayerFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    class MyViewHolder(binding: FavSongItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val songPic = binding.favSongPic
        val songName = binding.favSongName
        val cardView = binding.root
    }
}