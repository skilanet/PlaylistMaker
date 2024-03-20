package com.example.playlistmaker.ui.media_player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.domain.models.Song
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {

    private companion object {
        const val INTENT_PLAYLIST_KEY = "INTENT_PLAYLIST_KEY"
        const val DELAY = 300L
        const val STATE_DEFAULT = 0x000000
        const val STATE_PREPARED = 0x000001
        const val STATE_PLAYING = 0x000002
        const val STATE_PAUSED = 0x000003
    }

    private lateinit var binding: ActivityMediaPlayerBinding
    private lateinit var btnStartPause: ImageView
    private lateinit var tvNowTime: TextView
    private lateinit var handler: Handler
    private var url = ""
    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    var nowTime = 0L
    var elapsedTime = 0L

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

        Glide.with(this).load(song.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .into(binding.ivAlbumArtwork)
        binding.tvAlbumTextTop.text = song.collectionName
        binding.tvTrackAuthorTop.text = song.artistName
        binding.tvDurationText.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L)
        binding.tvAlbumText.text = song.collectionName
        binding.tvYearText.text = SimpleDateFormat("YYYY", Locale.getDefault()).format(293000L)
        binding.tvGenreText.text = song.primaryGenreName
        binding.tvCountryText.text = song.country
        preparePlayer()


        binding.ivBack.setOnClickListener {
            finish()
        }

        btnStartPause.setOnClickListener {
            playbackControl()
        }

    }

    private fun createSongFromJson(json: String): Song = Gson().fromJson(json, Song::class.java)

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            btnStartPause.setImageResource(R.drawable.play_image)
            elapsedTime = 0L
            nowTime = 0L
            stopTimer()
//            tvNowTime.text = getText(R.string.zero)
        }

    }

    private fun startPlayer() {
        mediaPlayer.start()
        btnStartPause.setImageResource(R.drawable.pause_image)
        playerState = STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        btnStartPause.setImageResource(R.drawable.play_image)
        playerState = STATE_PAUSED
        stopTimer()
    }

    private fun startTimer() {
        val startTime = System.currentTimeMillis()
        handler.post(
            createUpdateTimer(
                startTime - elapsedTime
            )
        )
    }

    private fun stopTimer() {
        handler.removeCallbacksAndMessages(null)
        tvNowTime.text = String.format("0:%02d", nowTime)
    }

    private fun createUpdateTimer(startTime: Long): Runnable {
        return object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                val seconds = elapsedTime / 1000L
                tvNowTime.text = String.format("0:%02d", seconds)
                nowTime = seconds
                this@MediaPlayerActivity.elapsedTime = elapsedTime
                handler.postDelayed(this, DELAY)
            }

        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}