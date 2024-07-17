package com.example.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.core.SongsAdapter
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.presentation.FavoriteTracksViewModel
import com.example.playlistmaker.media_library.ui.models.FavoriteTracksState
import com.example.playlistmaker.media_player.ui.MediaPlayerActivity
import com.example.playlistmaker.core.FragmentBinding
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : FragmentBinding<FragmentFavoriteTracksBinding>() {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteTracksBinding =
        FragmentFavoriteTracksBinding.inflate(layoutInflater, container, false)

    override fun setup() {}

    private val adapter = SongsAdapter()

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = FavoriteTracksFragment()
    }

    private val viewModel: FavoriteTracksViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.onItemClick = { song, _ -> onItemClick(song) }
        binding.rvFavoriteTracks.adapter = adapter
        binding.rvFavoriteTracks.layoutManager = LinearLayoutManager(requireActivity())
        viewModel.observeViewState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshTracks()
    }

    private fun renderState(newState: FavoriteTracksState) {
        when (newState) {
            is FavoriteTracksState.Content -> showFavorite(newState.data!!)
            is FavoriteTracksState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.rvFavoriteTracks.isVisible = false
        binding.groupEmptyContent.isVisible = true
    }

    private fun showFavorite(tracks: List<Song>) {
        binding.groupEmptyContent.isVisible = false
        binding.rvFavoriteTracks.isVisible = true
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun onItemClick(song: Song) {
        if (clickDebounce()) {
            val bundle = Bundle().apply {
                putString(MediaPlayerActivity.INTENT_PLAYLIST_KEY, Gson().toJson(song))
            }
            findNavController().navigate(
                R.id.action_fragmentMedia_to_mediaPlayerActivity, bundle
            )
        }
    }

    private var isClickAllowed = true

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }


}