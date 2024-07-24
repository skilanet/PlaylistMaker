package com.example.playlistmaker.playlist.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
import com.example.playlistmaker.playlist.ui.model.PlaylistState
import com.example.playlistmaker.playlist.ui.model.SongsInPlaylistState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
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
        layoutInflater: LayoutInflater, container: ViewGroup?
    ): FragmentPlaylistBinding = FragmentPlaylistBinding.inflate(layoutInflater, container, false)

    private lateinit var viewModel: PlaylistViewmodel
    private lateinit var deleteTrackDialog: MaterialAlertDialogBuilder
    private lateinit var deletePlaylistDialog: MaterialAlertDialogBuilder
    private var trackId by Delegates.notNull<Int>()
    private var playlistId by Delegates.notNull<Int>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun setup() {
        deleteTrackDialog = deleteDialogCreator(
            requireContext(),
            title = R.string.delete_tarck,
            message = R.string.are_you_sure_to_delete_track
        ) { viewModel.deleteTrackFromDB(trackId) }
        deletePlaylistDialog = deleteDialogCreator(
            requireContext(),
            title = R.string.delete_playlist,
            message = R.string.do_you_want_to_delete_plylist
        ) {
            viewModel.deletePlaylist(
                playlistId
            )
            findNavController().navigateUp()
        }
    }

    val adapter = SongsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = requireArguments().getInt(PLAYLIST_KEY)
        viewModel = getViewModel {
            parametersOf(playlistId)
        }

        viewModel.songsInPlaylistState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        viewModel.observeAboutTracksInPlaylistState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        adapter.onItemClick = { song: Song, _: Boolean -> onItemClick(song) }
        adapter.onLongItemClick = { currentId: Int ->
            trackId = currentId
            deleteTrackDialog.show()
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
            deletePlaylistDialog.show()
        }
        binding.tvEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_addPlaylistFragment, bundleOf(
                    EDIT_KEY to true, EDIT_ID_KEY to playlistId
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylist(playlistId)
    }

    private fun onItemClick(song: Song) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_mediaPlayerActivity,
            bundleOf(MediaPlayerActivity.INTENT_PLAYLIST_KEY to Gson().toJson(song))
        )
    }

    private fun renderState(state: PlaylistState) {
        when (state) {
            is PlaylistState.PlaylistReceived -> {
                with(state.playlist) {
                    setInfo(this)
                    setAdapter(tracks)
                }
            }
            else -> {}
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

    private fun setInfo(playlist: Playlist) {
        playlistId = playlist.id
        if (playlist.uri.isEmpty()) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.placeholder,
                requireContext().theme
            ).also {
                binding.ivPlaylistCover.setImageDrawable(it)
                binding.ivPlaylistCoverSmall.setImageDrawable(it)
            }
        } else {
            Uri.parse(playlist.uri).also {
                binding.ivPlaylistCover.setImageURI(it)
                binding.ivPlaylistCoverSmall.setImageURI(it)
            }
        }
        playlist.name.also {
            binding.tvPlaylistName.text = it
            binding.tvPlaylistNameSmall.text = it
        }
        binding.tvPlaylistDescription.apply {
            isVisible = !playlist.description.isNullOrBlank()
            if (isVisible) text = playlist.description.toString()
        }
    }

    private fun renderState(info: Pair<Int, Int>) {
        binding.tvPlaylistTime.text = resources.getQuantityString(
            R.plurals.playlist_time, info.first, info.first
        )
        resources.getQuantityString(R.plurals.tracks, info.second, info.second).also {
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
        adapter.tracks.clear()
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
                getString(R.string.this_playlist_not_contains_list_of_tracks),
                Toast.LENGTH_SHORT
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

    private fun deleteDialogCreator(
        context: Context,
        @StringRes title: Int,
        @StringRes message: Int,
        positiveAction: (() -> Unit)
    ): MaterialAlertDialogBuilder =
        MaterialAlertDialogBuilder(context, R.style.MyAlertDialogTheme).setTitle(title)
            .setMessage(message).setNeutralButton(R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                positiveAction()
            }
}