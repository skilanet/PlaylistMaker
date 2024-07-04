package com.example.playlistmaker.new_playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.util.FragmentBinding

class AddPlaylistFragment : FragmentBinding<FragmentCreatePlaylistBinding>() {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePlaylistBinding =
        FragmentCreatePlaylistBinding.inflate(layoutInflater, container, false)

    override fun setupListeners() {}
}