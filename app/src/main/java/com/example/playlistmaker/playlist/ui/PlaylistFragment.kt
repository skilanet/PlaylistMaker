package com.example.playlistmaker.playlist.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.core.FragmentBinding
import com.example.playlistmaker.core.SongsAdapter
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_player.ui.MediaPlayerActivity
import com.example.playlistmaker.playlist.presentation.PlaylistViewmodel
import com.example.playlistmaker.playlist.ui.model.SongsInPlaylistState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.abs
import kotlin.properties.Delegates

class PlaylistFragment : FragmentBinding<FragmentPlaylistBinding>() {

    companion object {
        const val PLAYLIST_KEY = "playlist_key"
        const val EDIT_KEY = "edit_key"
        const val EDIT_ID_KEY = "edit_id_key"
    }

    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding = FragmentPlaylistBinding.inflate(layoutInflater, container, false)

    private lateinit var viewModel: PlaylistViewmodel
    private lateinit var deleteDialog: MaterialAlertDialogBuilder
    private var trackId by Delegates.notNull<Int>()
    private var playlistId by Delegates.notNull<Int>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun setup() {
        deleteDialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
            .setTitle(R.string.delete_tarck)
            .setMessage(R.string.are_you_sure_to_delete_track)
            .setNeutralButton(R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrackFromDB(trackId)
            }
    }

    val adapter = SongsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = requireArguments().getInt(PLAYLIST_KEY)

        viewModel = getViewModel {
            parametersOf(id)
        }

        viewModel.songsInPlaylistState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        renderState()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        adapter.onItemClick = { song: Song, _: Boolean -> onItemClick(song) }
        adapter.onLongItemClick = { currentId: Int ->
            Log.d("_TAG", "id = $currentId")
            trackId = currentId
            deleteDialog.show()
        }
        binding.rvPlaylistTracks.adapter = adapter
        binding.rvPlaylistTracks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.ivSharePlaylist.setOnClickListener {
            sharePlaylist(requireContext())
        }

        val tracksBottomSheet = binding.llBottomSheetTracks
        val bottomSheetContainer = binding.clBottomSheetMore
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(p0: View, p1: Int) {}

                override fun onSlide(p0: View, p1: Float) {
                    tracksBottomSheet.alpha = abs(p1)
                }
            })
        }
        binding.ivMore.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.tvShare.setOnClickListener {
            sharePlaylist(requireContext())
        }
        binding.tvDelete.setOnClickListener {
            findNavController().navigateUp()
//            viewModel.deletePlaylist()
        }
        binding.tvEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_addPlaylistFragment, bundleOf(
                    EDIT_KEY to true,
                    EDIT_ID_KEY to playlistId
                )
            )
        }
    }

    private fun onItemClick(song: Song) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_mediaPlayerActivity,
            bundleOf(MediaPlayerActivity.INTENT_PLAYLIST_KEY to Gson().toJson(song))
        )
    }

    private fun renderState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playlistState.filterNotNull().collectLatest {
                setInfo(it.first, it.second)
                setAdapter(it.first.tracks)
            }
        }
    }

    private fun renderState(state: SongsInPlaylistState) {
        when (state) {
            is SongsInPlaylistState.Empty -> {
                showEmpty()
            }

            is SongsInPlaylistState.Content -> {
                setAdapter(state.data)
            }
        }
    }

    private fun setInfo(playlist: Playlist, time: Int) {
        playlistId = playlist.id
        binding.ivPlaylistCover.setImageURI(Uri.parse(playlist.uri))
        binding.ivPlaylistCoverSmall.setImageURI(Uri.parse(playlist.uri))
        playlist.name.also {
            binding.tvPlaylistName.text = it
            binding.tvPlaylistNameSmall.text = it
        }
        binding.tvPlaylistDescription.apply {
            isVisible = !playlist.description.isNullOrBlank()
            if (isVisible) text = playlist.description.toString()
        }
        binding.tvPlaylistTime.text = resources.getQuantityString(
            R.plurals.playlist_time, time, time
        )
        val tracks = playlist.countOfTracks
        resources.getQuantityString(R.plurals.tracks, tracks, tracks).also {
            binding.tvPlaylistCountOfTracks.text = it
            binding.tvPlaylistCountOfTracksSmall.text = it
        }
    }

    private fun setAdapter(tracks: List<Song>) {
        if (tracks.isEmpty()) {
            showEmpty()
            return
        }
        showContent()
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.rvPlaylistTracks.isVisible = false
        binding.tvPlaylistNotContainsTracks.isVisible = true
        binding.ivErrorImage.isVisible = true
    }

    private fun showContent() {
        binding.rvPlaylistTracks.isVisible = true
        binding.tvPlaylistNotContainsTracks.isVisible = false
        binding.ivErrorImage.isVisible = false
    }

    private fun sharePlaylist(context: Context) {
        val tracks = adapter.tracks
        if (tracks.isEmpty()) {
            Toast.makeText(
                context,
                getString(R.string.this_playlist_not_contains_list_of_tracks), Toast.LENGTH_SHORT
            ).show()
            return
        }
        var message = "${binding.tvPlaylistName.text}\n${binding.tvPlaylistCountOfTracks.text}\n"
        tracks.forEachIndexed { index, song ->
            message += "${index + 1}. ${song.artistName} - ${song.trackName} (${song.currentTime})"
            if (index != tracks.lastIndex) message += "\n"
        }
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
            startActivity(this)
        }
    }
}