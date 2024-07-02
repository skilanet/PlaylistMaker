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
import com.example.playlistmaker.media_player.ui.models.FavoriteState
import com.example.playlistmaker.media_player.ui.models.PlayingState
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MediaPlayerActivity : AppCompatActivity() {

    companion object {
        const val INTENT_PLAYLIST_KEY = "INTENT_PLAYLIST_KEY"
    }

    private lateinit var viewModel: MediaPlayerViewModel

    private lateinit var binding: ActivityMediaPlayerBinding
    private lateinit var btnStartPause: ImageView
    private lateinit var tvNowTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val song = createSongFromJson(intent.extras!!.getString(INTENT_PLAYLIST_KEY)!!)
        btnStartPause = binding.ivStopPlayButton
        tvNowTime = binding.tvNowTime

        viewModel = getViewModel {
            parametersOf(song)
        }

        viewModel.observePlayingState().observe(this) { state ->
            renderState(state)
        }

        viewModel.observeFavoriteState().observe(this) { state ->
            renderState(state)
        }

        btnStartPause.setOnClickListener {
            viewModel.onButtonClicked()
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

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}