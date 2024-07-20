package com.example.playlistmaker.media_player.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.core.PermissionRequests
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.main.ui.RootActivity
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.ui.models.PlaylistsState
import com.example.playlistmaker.media_player.presentation.view_model.MediaPlayerViewModel
import com.example.playlistmaker.media_player.ui.models.FavoriteState
import com.example.playlistmaker.media_player.ui.models.InPlaylistState
import com.example.playlistmaker.media_player.ui.models.PlayingState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MediaPlayerActivity : AppCompatActivity() {

    companion object {
        const val INTENT_PLAYLIST_KEY = "INTENT_PLAYLIST_KEY"
        const val FRAGMENT_KEY = "FRAGMENT_KEY"
        const val FRAGMENT_CODE = 1
    }

    private lateinit var viewModel: MediaPlayerViewModel
    private lateinit var binding: ActivityMediaPlayerBinding
    private lateinit var btnStartPause: ImageView
    private lateinit var tvNowTime: TextView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val adapter = PlaylistsAdapter(this)
    private var _permissionFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val song = createSongFromJson(intent.extras!!.getString(INTENT_PLAYLIST_KEY)!!)
        btnStartPause = binding.ivStopPlayButton
        tvNowTime = binding.tvNowTime

        adapter.onItemClick = { playlist -> onItemClick(playlist, song) }

        binding.rvMediaPlayerAddToPlaylist.adapter = adapter
        binding.rvMediaPlayerAddToPlaylist.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewModel = getViewModel {
            parametersOf(song)
        }

        viewModel.observePlayingState().observe(this) { state ->
            renderState(state)
        }

        viewModel.observeFavoriteState().observe(this) { state ->
            renderState(state)
        }

        viewModel.observeInPlaylistState().observe(this) { state ->
            renderState(state)
        }

        viewModel.observePlaylistsState().observe(this) { state ->
            renderState(state)
        }

        btnStartPause.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        Glide.with(this.applicationContext).load(song.artworkUrl512)
            .into(binding.ivAlbumArtwork)
        binding.tvAlbumTextTop.text = song.collectionName
        binding.tvTrackAuthorTop.text = song.artistName
        binding.tvDurationText.text = song.currentTime
        binding.tvAlbumText.text = song.collectionName
        binding.tvYearText.text = song.currentDate
        binding.tvGenreText.text = song.primaryGenreName
        binding.tvCountryText.text = song.country

        binding.ivLike.setOnClickListener {
            viewModel.onLikeClicked()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
        val bottomSheetContainer = binding.llBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.ivAddToPlayList.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.btnNewPlaylist.setOnClickListener {
            val requester = PermissionRequester.instance()
            lifecycleScope.launch {
                requester.request(*PermissionRequests.PERMISSIONS).collect { result ->
                    when (result) {
                        is PermissionResult.Granted -> {
                            _permissionFlag = true
                        }

                        is PermissionResult.Denied.DeniedPermanently -> {
                            _permissionFlag = false
                            PermissionRequests.goToSettings(
                                this@MediaPlayerActivity
                            )
                        }

                        else -> {}
                    }
                }
            }

            if (_permissionFlag) {
                Intent(this@MediaPlayerActivity, RootActivity::class.java).apply {
                    putExtra(FRAGMENT_KEY, FRAGMENT_CODE)
                    startActivity(this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }

    private fun createSongFromJson(json: String): Song = Gson().fromJson(json, Song::class.java)
    private fun renderState(state: PlayingState) {
        btnStartPause.isEnabled = state.isButtonEnable
        btnStartPause.setImageDrawable(AppCompatResources.getDrawable(this, state.buttonBackground))
        tvNowTime.text = state.currentTime
    }

    private fun renderState(state: FavoriteState) {
        binding.ivLike.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                when (state) {
                    is FavoriteState.InFavorite -> R.drawable.like_image_filled
                    is FavoriteState.NotInFavorite -> R.drawable.like_image_unfilled
                }
            )
        )
    }

    private fun renderState(state: InPlaylistState) {
        binding.ivAddToPlayList.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                when (state) {
                    is InPlaylistState.InPlaylist -> R.drawable.added_to_playlist_image
                    is InPlaylistState.NotInPlaylist -> R.drawable.add_to_playlist_image
                }
            )
        )
    }

    private fun renderState(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> binding.rvMediaPlayerAddToPlaylist.isVisible = false
            is PlaylistsState.Content -> showPlaylists(state.data)
        }
    }

    private fun onItemClick(playlist: Playlist, song: Song) {
        if (playlist.tracks.contains(song)) Toast.makeText(
            this,
            getString(R.string.this_track_already_in_playlist, playlist.name), Toast.LENGTH_SHORT
        ).show()
        else {
            with(viewModel) {
                insertSong(playlistId = playlist.id, song = song)
                refreshPlaylists()
            }
            Toast.makeText(
                this,
                getString(R.string.added_to_playlist, playlist.name),
                Toast.LENGTH_SHORT
            ).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        binding.rvMediaPlayerAddToPlaylist.isVisible = true
        adapter.playlists.clear()
        adapter.playlists.addAll(playlists)
        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}