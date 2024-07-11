package com.example.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.core.FragmentBinding
import com.example.playlistmaker.core.PermissionRequests
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.presentation.PlaylistsViewModel
import com.example.playlistmaker.media_library.ui.models.PlaylistsState
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : FragmentBinding<FragmentPlaylistsBinding>() {

    override fun createBinding(
        layoutInflater: LayoutInflater, container: ViewGroup?
    ): FragmentPlaylistsBinding = FragmentPlaylistsBinding.inflate(layoutInflater, container, false)

    private lateinit var adapter: PlaylistsAdapter
    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun setup() {
        binding.btnCreatePlaylist.setOnClickListener {
            val requester = PermissionRequester.instance()
            viewLifecycleOwner.lifecycleScope.launch {
                requester.request(*PermissionRequests.PERMISSIONS).collect { result ->
                    when(result) {
                        is PermissionResult.Granted -> findNavController().navigate(R.id.action_fragmentMedia_to_addPlaylistFragment)
                        is PermissionResult.Denied.DeniedPermanently -> PermissionRequests.goToSettings(requireContext())
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlaylistsAdapter(requireContext())
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