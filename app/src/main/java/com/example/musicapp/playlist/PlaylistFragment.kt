package com.example.musicapp.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.musicapp.MusicPlaylist
import com.example.musicapp.PlayerFragment
import com.example.musicapp.PlayerFragment.Companion
import com.example.musicapp.PlayerFragment.Companion.min15
import com.example.musicapp.PlayerFragment.Companion.min30
import com.example.musicapp.PlayerFragment.Companion.min60
import com.example.musicapp.Playlist
import com.example.musicapp.R
import com.example.musicapp.databinding.AddPlaylistDialogBinding
import com.example.musicapp.databinding.FragmentPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PlaylistFragment : Fragment() {

    lateinit var binding: FragmentPlaylistBinding
    lateinit var adapter: PlaylistAdapter

    companion object {
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        adapter = PlaylistAdapter(requireActivity(), playlistList = musicPlaylist.ref)


//        val tempList = ArrayList<String>()
//        tempList.add("travel songs")
//        tempList.add("Lets enjoy the music")
//        tempList.add("travel songs for train")
//        tempList.add("travel songs")
//        tempList.add("travel songs")
//        tempList.add("travel songs")
//        tempList.add("travel songs")
//        tempList.add("travel songs")


        binding.playListRecyclerView.adapter =
            PlaylistAdapter(requireActivity(), playlistList = musicPlaylist.ref)

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.addBtnPlaylist.setOnClickListener {
            customAlertDialog()
        }


        return binding.root
    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(customDialog)
            .setMessage("Playlist Details")
            .setPositiveButton("ADD")
            { dialog, _ ->
                val playlistName = binder.playlistNameText.text
                val createdBy = binder.yourNameText.text
                if (playlistName != null && createdBy != null) {
                    if (playlistName.isNotEmpty() && createdBy.isNotEmpty()) {
                        addPlaylist(playlistName.toString(), createdBy.toString())
                    }
                }
                dialog.dismiss()
            }.show()
    }

    private fun addPlaylist(name: String, createdBy: String) {

        var playlistExists = false
        for (i in musicPlaylist.ref) {
            if (name.equals(i.name)) {
                playlistExists = true
                break
            }
        }
        if (playlistExists) {
            Toast.makeText(requireContext(), "Playlist Exists!!", Toast.LENGTH_SHORT).show()
        } else {
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

            tempPlaylist.createdOn = sdf.format(calendar)

            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()

        }
    }

}