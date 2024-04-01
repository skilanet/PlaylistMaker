package com.example.playlistmaker.media_player.data.impl

import android.media.MediaPlayer
import android.os.Handler
import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor

class MediaPlayerInteractorImpl(
    private val url: String,
    private val handler: Handler
) : MediaPlayerInteractor {

    private companion object {
        const val DELAY = 300L
        const val STATE_DEFAULT = 0x000000
        const val STATE_PREPARED = 0x000001
        const val STATE_PLAYING = 0x000002
        const val STATE_PAUSED = 0x000003
    }

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    override var currentTime: Int = 0
    private var timeControl: Runnable? = null


    override fun prepare() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            if (timeControl != null) handler.removeCallbacks(timeControl!!)
            currentTime = 0
            playerState = STATE_PREPARED
        }
    }

    override fun start() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        timeControl = timeControl()
        handler.post(timeControl!!)
    }

    override fun pause() {
        currentTime = mediaPlayer.currentPosition
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        if (timeControl != null) handler.removeCallbacks(timeControl!!)
    }

    override fun stop() {
        mediaPlayer.stop()
        currentTime = 0
    }

    override fun timeControl(): Runnable {
        return object : Runnable {
            override fun run() {
                currentTime = mediaPlayer.currentPosition
                handler.postDelayed(this, DELAY)
            }
        }
    }

    override fun release() = mediaPlayer.release()
}