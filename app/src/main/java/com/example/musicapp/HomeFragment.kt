package com.example.musicapp

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import kotlin.system.exitProcess


class HomeFragment : Fragment() {

    lateinit var favBtn: Button
    lateinit var shuffleBtn: Button
    lateinit var playlistBtn: Button
    lateinit var totalSongs: TextView
    lateinit var recyclerView: RecyclerView

    companion object {
        lateinit var musicList: ArrayList<Music>
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        requestRuntimePermission()

        favBtn = view.findViewById(R.id.favBtn)
        shuffleBtn = view.findViewById(R.id.shuffleBtn)
        playlistBtn = view.findViewById(R.id.playlistBtn)
        recyclerView = view.findViewById(R.id.songs_recyler_view)
        totalSongs = view.findViewById(R.id.totalSongs)


        musicList = getAllAudio()


        totalSongs.text = "Total Songs: " + musicList.size


        recyclerView.adapter =
            MusicAdapter(requireContext(), musicList, activity = requireActivity())


        favBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, FavoriteFragment::class.java, null)
                .addToBackStack(null)
                .commit()
        }

        shuffleBtn.setOnClickListener {

            val bundle = Bundle()
            bundle.putInt("index", 0)
            bundle.putString("class", "HomeFragment")
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, PlayerFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }

        playlistBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, PlaylistFragment::class.java, null)
                .addToBackStack(null)
                .commit()
        }

        return view.rootView
    }

    // for requesting permission
    fun requestRuntimePermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                AppConstants.REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // agar multiple permission hogi to vo phli wali se check krega jo ki 0 index pr hogi
        if (requestCode == AppConstants.REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    AppConstants.REQUEST_CODE
                )
            }
        }
    }

    fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " !=0 "
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = requireActivity().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC", null
        )
        if (cursor != null) {
            if (cursor.moveToFirst())
                do {

                    val title =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                    val id =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val album =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                    val artist =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                    val path =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    val duration =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    val albumId =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUri = Uri.withAppendedPath(uri, albumId).toString()

                    val music = Music(id, title, album, artist, duration, path, artUri)
                    val file = File(music.path)
                    if (file.exists()) {
                        tempList.add(music)
                    }

                } while (cursor.moveToNext())
        }
        cursor?.close()

        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        if (PlayerFragment.isPlaying == false) {
            PlayerFragment.musicService?.stopForeground(true)
            PlayerFragment.musicService?.mediaPlayer?.release()
            exitProcess(1)


        }
    }
}
