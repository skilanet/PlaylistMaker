package com.example.playlistmaker.media_player.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_player.presentation.view_model.MediaPlayerViewModel
import com.example.playlistmaker.media_player.ui.models.PlayingState
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaPlayerActivity : AppCompatActivity() {

    companion object {
        const val SONG_TRANS_KEY = "song_trans_key"
    }

    private lateinit var binding: ActivityMediaPlayerBinding
    private lateinit var btnStartPause: ImageView
    private lateinit var tvNowTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val song = createSongFromJson(intent.getStringExtra(SONG_TRANS_KEY)!!)
        val url = song.previewUrl
        btnStartPause = binding.ivStopPlayButton
        tvNowTime = binding.tvNowTime

        val viewModel: MediaPlayerViewModel by viewModel {
            parametersOf(url)
        }

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