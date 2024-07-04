package com.example.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        binding.btnCreatePlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentMedia_to_addPlaylistFragment)
        }
        return binding.root
    }

    companion object{
        fun newInstance() = PlaylistsFragment()
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}