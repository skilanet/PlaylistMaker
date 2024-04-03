package com.example.playlistmaker.media_player.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor

class MediaPlayerInteractorImpl(
    private val url: String,
) : MediaPlayerInteractor {

    private companion object {
        const val STATE_DEFAULT = 0x000000
        const val STATE_PREPARED = 0x000001
        const val STATE_PLAYING = 0x000002
        const val STATE_PAUSED = 0x000003
    }

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    override var state = STATE_DEFAULT


    override fun prepare() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            state = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            state = STATE_PREPARED
        }
    }

    override fun start() {
        mediaPlayer.start()
        state = STATE_PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        state = STATE_PAUSED
    }

    override fun getCurrentTime(): Int = mediaPlayer.currentPosition

    override fun release() {
        mediaPlayer.release()
    }
}