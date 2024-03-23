package com.example.playlistmaker.presentation.media_player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.domain.models.Song
import com.example.playlistmaker.domain.repository.MediaPlayerInteractor
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {

    private companion object {
        const val INTENT_PLAYLIST_KEY = "INTENT_PLAYLIST_KEY"
    }

    private lateinit var binding: ActivityMediaPlayerBinding
    private lateinit var btnStartPause: ImageView
    private lateinit var tvNowTime: TextView
    private lateinit var handler: Handler
    private lateinit var mediaPlayerInteractor: MediaPlayerInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val songString = intent.getStringExtra(INTENT_PLAYLIST_KEY)
        val song = createSongFromJson(songString!!)
        btnStartPause = binding.ivStopPlayButton
        tvNowTime = binding.tvNowTime
        handler = Handler(Looper.getMainLooper())
        mediaPlayerInteractor = Creator.provideMediaPlayerInteractor(song.previewUrl, handler)
        mediaPlayerInteractor.setString = { s ->
            binding.tvDurationText.text = s
        }
        mediaPlayerInteractor.setImage = { isPlaying: Boolean ->
            run {
                if (isPlaying) binding.ivStopPlayButton.setImageResource(R.drawable.pause_image)
                else binding.ivStopPlayButton.setImageResource(R.drawable.play_image)
            }
        }

        Glide.with(this).load(song.artworkUrl512)
            .into(binding.ivAlbumArtwork)
        binding.tvAlbumTextTop.text = song.collectionName
        binding.tvTrackAuthorTop.text = song.artistName
        binding.tvDurationText.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L)
        binding.tvAlbumText.text = song.collectionName
        binding.tvYearText.text = SimpleDateFormat("YYYY", Locale.getDefault()).format(293000L)
        binding.tvGenreText.text = song.primaryGenreName
        binding.tvCountryText.text = song.country
        mediaPlayerInteractor.prepare()


        binding.ivBack.setOnClickListener {
            finish()
        }

        btnStartPause.setOnClickListener {
            mediaPlayerInteractor.playBackControl()
        }

    }

    private fun createSongFromJson(json: String): Song = Gson().fromJson(json, Song::class.java)

    override fun onPause() {
        super.onPause()
        mediaPlayerInteractor.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}