package com.example.playlistmaker.media_library.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.presentation.PlaylistsViewModel
import com.example.playlistmaker.media_library.ui.models.PlaylistsState
import com.example.playlistmaker.util.FragmentBinding
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : FragmentBinding<FragmentPlaylistsBinding>() {

    override fun createBinding(
        layoutInflater: LayoutInflater, container: ViewGroup?
    ): FragmentPlaylistsBinding = FragmentPlaylistsBinding.inflate(layoutInflater, container, false)

    private val adapter = PlaylistsAdapter()
    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun setup() {
        val requester = PermissionRequester.instance()
        lifecycleScope.launch {
            requester.request(
                Manifest.permission.CAMERA
            ).collect { result ->
                when (result) {
                    is PermissionResult.Granted -> {
                    }

                    is PermissionResult.Denied.DeniedPermanently -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", requireContext().packageName, null)
                        startActivity(this)
                    }

                    is PermissionResult.Denied.NeedsRationale -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.permission_is_required_to_create_playlist),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is PermissionResult.Cancelled -> {}
                }
            }
        }
        binding.btnCreatePlaylist.setOnClickListener { findNavController().navigate(R.id.action_fragmentMedia_to_addPlaylistFragment) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPlaylists.adapter = adapter
        binding.rvPlaylists.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }


    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private fun renderState(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showPlaceholder()
            is PlaylistsState.Content -> showContent(state.data)
        }
    }

    private fun showPlaceholder() {
        binding.rvPlaylists.isVisible = false
        binding.groupNoPlaylists.isVisible = true
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.groupNoPlaylists.isVisible = false
        adapter.playlists.clear()
        adapter.playlists.addAll(playlists)
        adapter.notifyDataSetChanged()
        binding.rvPlaylists.isVisible = true
    }
}