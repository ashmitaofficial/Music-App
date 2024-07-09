package com.example.musicapp.playlist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.musicapp.AppConstants
import com.example.musicapp.MusicPlaylist
import com.example.musicapp.PlayerFragment
import com.example.musicapp.R
import com.example.musicapp.checkPlaylist
import com.example.musicapp.databinding.FragmentFavoriteBinding
import com.example.musicapp.databinding.FragmentPlaylistBinding
import com.example.musicapp.databinding.FragmentPlaylistDetailsBinding
import com.example.musicapp.databinding.PlaylistItemBinding
import com.example.musicapp.favorite.FavoriteFragment
import com.example.musicapp.home.HomeFragment
import com.example.musicapp.home.MusicAdapter
import com.example.musicapp.selection.SelectionActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso

class PlaylistDetailsFragment : Fragment() {

    lateinit var binding: FragmentPlaylistDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object {
        var currentPlaylistPosition: Int? = -1
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)

        currentPlaylistPosition =
            arguments?.getInt(AppConstants.PLAYLIST_POSITION)

        PlaylistFragment.musicPlaylist.ref[currentPlaylistPosition!!].playlist= checkPlaylist(PlaylistFragment.musicPlaylist.ref[currentPlaylistPosition!!].playlist)

//
        currentPlaylistPosition?.let {
//            PlaylistFragment.musicPlaylist.ref[it].playlist.addAll(HomeFragment.musicList)
            adapter = MusicAdapter(
                requireContext(),
                PlaylistFragment.musicPlaylist.ref[it].playlist,
                requireActivity(),
                true
            )
            binding.playlistDetailsRecyclerView.adapter = adapter

            binding.shuffleBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("index", 0)
                bundle.putString("class", "PlaylistDetailShuffle")
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.container, PlayerFragment::class.java, bundle)
                    .addToBackStack(null)
                    .commit()
            }

            binding.AddSongsBtn.setOnClickListener {
                val intent = Intent(requireContext(), SelectionActivity::class.java)
                startActivity(intent)
            }

            binding.removeSongsBtn.setOnClickListener {

                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle("Remove song")
                    .setMessage("Do you want to remove all songs from playlist?")
                    .setPositiveButton("Yes")
                    { dialog, _ ->
                        PlaylistFragment.musicPlaylist.ref[currentPlaylistPosition!!].playlist.clear()
                        adapter.refreshPlaylist()
                        dialog.dismiss()
                    }
                    .setNegativeButton("No"){ dialog,_ ->
                        dialog.dismiss()
                    }
            }

        }



        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()

        currentPlaylistPosition?.let {
            binding.playlistNamePD.text =
                PlaylistFragment.musicPlaylist.ref[it].name
            binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n" +
                    "Created On:\n${PlaylistFragment.musicPlaylist.ref[it].createdOn}\n\n" +
                    "--${PlaylistFragment.musicPlaylist.ref[it].createdBy}"

            if (adapter.itemCount > 0) {
                Picasso.get()
                    .load(PlaylistFragment.musicPlaylist.ref[it].playlist[0].artUri)
                    .placeholder(R.drawable.song_pic)
                    .into(binding.playlistImgPD)

                binding.shuffleBtn.visibility = View.VISIBLE

            }

        }


//        PlaylistFragment.musicPlaylist= MusicPlaylist()
        val editor = requireActivity().getSharedPreferences("FAVORITES", Context.MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistFragment.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)
        editor.apply()





    }


}