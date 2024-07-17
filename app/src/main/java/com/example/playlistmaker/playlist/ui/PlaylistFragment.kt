package com.example.playlistmaker.playlist.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.core.FragmentBinding
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.playlist.presentation.PlaylistViewmodel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : FragmentBinding<FragmentPlaylistBinding>() {

    companion object {
        const val PLAYLIST_KEY = "playlist_key"
    }

    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding = FragmentPlaylistBinding.inflate(layoutInflater, container, false)

    override fun setup() = Unit

    val viewModel: PlaylistViewmodel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlist =
            Gson().fromJson(requireArguments().getString(PLAYLIST_KEY), Playlist::class.java)
        binding.ivPlaylistCover.setImageURI(Uri.parse(playlist.uri))
        binding.tvPlaylistName.text = playlist.name
        binding.tvPlaylistDescription.apply {
            isVisible = !playlist.description.isNullOrBlank()
            if (isVisible) text = playlist.description.toString()
        }
        viewModel.calculateTime(playlist.tracks).also {
            binding.tvPlaylistTime.text = requireContext().resources.getQuantityString(
                R.plurals.playlist_time, it, it
            )
        }
        playlist.countOfTracks.also {
            binding.tvPlaylistCountOfTracks.text =
                requireContext().resources.getQuantityString(R.plurals.tracks, it, it)
        }
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }


}