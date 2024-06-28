package com.example.musicapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.musicapp.databinding.FragmentFavoriteBinding
import com.example.musicapp.databinding.FragmentPlayerBinding

class FavoriteFragment : Fragment() {
    lateinit var backBtn: ImageButton

    lateinit var binding: FragmentFavoriteBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }


        return binding.root
    }


}