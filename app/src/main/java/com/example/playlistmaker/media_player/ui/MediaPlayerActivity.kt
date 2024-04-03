package com.example.playlistmaker.media_player.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_player.presentation.view_model.MediaPlayerViewModel
import com.example.playlistmaker.media_player.ui.models.PlayingState
import com.google.gson.Gson

class MediaPlayerActivity : AppCompatActivity() {

    private companion object {
        const val INTENT_PLAYLIST_KEY = "INTENT_PLAYLIST_KEY"
    }

    private lateinit var binding: ActivityMediaPlayerBinding
    private lateinit var btnStartPause: ImageView
    private lateinit var tvNowTime: TextView
    private lateinit var viewModel: MediaPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val song = createSongFromJson(intent.getStringExtra(INTENT_PLAYLIST_KEY)!!)
        btnStartPause = binding.ivStopPlayButton
        tvNowTime = binding.tvNowTime
        viewModel = ViewModelProvider(
            this,
            MediaPlayerViewModel.getViewModelFactory(
                song.previewUrl
            )
        )[MediaPlayerViewModel::class.java]
        viewModel.observePlayingState().observe(this) {
            setImage(it)
            viewModel.stateControl()
        }
        viewModel.observeTimeState().observe(this) {
            binding.tvNowTime.text = it
            viewModel.updateTimeState()
        }

        // set info
        Glide.with(this.applicationContext).load(song.artworkUrl512)
            .into(binding.ivAlbumArtwork)
        binding.tvAlbumTextTop.text = song.collectionName
        binding.tvTrackAuthorTop.text = song.artistName
        binding.tvDurationText.text = song.currentTime
        binding.tvAlbumText.text = song.collectionName
        binding.tvYearText.text = song.currentDate
        binding.tvGenreText.text = song.primaryGenreName
        binding.tvCountryText.text = song.country


        binding.ivBack.setOnClickListener {
            finish()
        }

        viewModel.onPrepare()
        btnStartPause.setOnClickListener {
            viewModel.playingControl()
        }

    }

    private fun createSongFromJson(json: String): Song = Gson().fromJson(json, Song::class.java)

    private fun setImage(state: PlayingState) {
        btnStartPause.setImageDrawable(
            AppCompatResources.getDrawable(
                this, when (state) {
                    is PlayingState.Default,
                    PlayingState.Prepared,
                    PlayingState.Paused,
                    PlayingState.Complete -> R.drawable.play_image

                    is PlayingState.Playing -> R.drawable.pause_image
                }
            )
        )
    }
}