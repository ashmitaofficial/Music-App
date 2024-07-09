package com.example.musicapp.playlist

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.AppConstants
import com.example.musicapp.PlayerFragment.Companion.binding
import com.example.musicapp.PlayerFragment.Companion.min15
import com.example.musicapp.PlayerFragment.Companion.min30
import com.example.musicapp.PlayerFragment.Companion.min60
import com.example.musicapp.Playlist
import com.example.musicapp.R
import com.example.musicapp.databinding.PlaylistItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistAdapter(
    private val context: FragmentActivity,
    private var playlistList: ArrayList<Playlist>
) : RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistAdapter.MyViewHolder {
        return MyViewHolder(
            PlaylistItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlaylistAdapter.MyViewHolder, position: Int) {
        holder.playlistName.text = playlistList[position].name
        holder.playlistName.isSelected = true

        holder.playlistBin.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Do you want to delete the playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistFragment.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }

        holder.cardview.setOnClickListener {
            val intent= Intent(context,PlaylistDetailsActivity::class.java)
            intent.putExtra(AppConstants.PLAYLIST_POSITION,position)
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    class MyViewHolder(binding: PlaylistItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val playlistPic = binding.playlistImg
        val playlistName = binding.playlistName
        val playlistBin = binding.binBtn
        val cardview = binding.root


    }

    fun refreshPlaylist() {
        playlistList = ArrayList()
        playlistList.addAll(PlaylistFragment.musicPlaylist.ref)
        notifyDataSetChanged()

    }
}