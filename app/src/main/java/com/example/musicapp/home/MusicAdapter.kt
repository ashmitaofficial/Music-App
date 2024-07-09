package com.example.musicapp.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.Music
import com.example.musicapp.PlayerFragment
import com.example.musicapp.R
import com.example.musicapp.databinding.MusicItemBinding
import com.example.musicapp.playlist.PlaylistDetailsFragment
import com.example.musicapp.playlist.PlaylistFragment
import com.example.musicapp.selection.SelectionActivity
import com.squareup.picasso.Picasso


class MusicAdapter(
    private val context: Context,
    private var musicList: ArrayList<Music>,
    val activity: FragmentActivity,
    private val playListDetails: Boolean = false,
    private val selectionActivity: Boolean = false
) :
    RecyclerView.Adapter<MusicAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(MusicItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.song_name.text = musicList[position].title
        holder.movie_name.text = musicList[position].artist
        holder.duration.text = musicList[position].duration?.let { it1 -> convertDuration(it1) }
//      Picasso.get().load(musicList[position].album).into(holder.image)
        Picasso.get().load(musicList[position].album).placeholder(R.drawable.song_pic)
            .into(holder.image)

        when{
            playListDetails ->{
                holder.cardView.setOnClickListener {
                    sendIntent("PlaylistDetailsAdapter", position)

                }
            }
        }
        holder.cardView.setOnClickListener {
            when {
                HomeFragment.search ->
                    sendIntent("MusicAdapterSearch", position)

                selectionActivity -> {
                    holder.cardView.setOnClickListener {
                        if (addSong(musicList[position])) {
                            holder.cardView.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.cool_pink
                                )
                            )

                        } else {
                            holder.cardView.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )
                        }
                    }
                }

                else ->
                    sendIntent("MusicAdapter", position)
            }
        }


    }

    private fun addSong(song: Music): Boolean {
        PlaylistFragment.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPosition!!].playlist.forEachIndexed { index, music ->
            if (song.id == music.id) {
                PlaylistFragment.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPosition!!].playlist.removeAt(
                    index
                )
                return false
            }


        }
        PlaylistFragment.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPosition!!].playlist.add(
            song
        )
        return true
    }

    override fun getItemCount(): Int {
        return musicList.size
    }


    class MyViewHolder(binding: MusicItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val song_name = binding.songName
        val movie_name = binding.movieName
        val image = binding.songPic
        val duration = binding.songDuration
        val cardView = binding.cardview

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

    fun updateMusicList(searchList: ArrayList<Music>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int) {
        val bundle = Bundle()
        bundle.putInt("index", pos)
        bundle.putString("class", ref)

        activity.supportFragmentManager.beginTransaction()
            .add(R.id.container, PlayerFragment::class.java, bundle)
            .addToBackStack(null)
            .commit()

    }

    fun refreshPlaylist() {
        musicList = ArrayList()
        musicList = PlaylistFragment.musicPlaylist.ref[
            PlaylistDetailsFragment.currentPlaylistPosition!!].playlist
        notifyDataSetChanged()
    }

}