package com.example.playlistmaker.data.repository

import android.app.Activity
import android.media.MediaPlayer
import android.os.Handler
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.repository.MediaPlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerInteractorImpl(private val url: String,
    private val handler: Handler): MediaPlayerInteractor {

    private companion object {
        const val DELAY = 300L
        const val STATE_DEFAULT = 0x000000
        const val STATE_PREPARED = 0x000001
        const val STATE_PLAYING = 0x000002
        const val STATE_PAUSED = 0x000003
    }

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    var currentTime: Int? = null
    private var timeControl: Runnable? = null
    override var setString: ((String) -> Unit)? = null
    override var setImage: ((Boolean) -> Unit)? = null


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
            setString!!.invoke(Activity().getString(R.string.zero))
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
        setString!!.invoke(Activity().getString(R.string.zero))
    }

    override fun timeControl(): Runnable {
        return object : Runnable {
            override fun run() {
                currentTime = mediaPlayer.currentPosition
                setString!!.invoke(SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTime))
                handler.postDelayed(this, DELAY)
            }
        }
    }

    override fun playBackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pause()
                setImage!!.invoke(true)
            }
            STATE_PREPARED, STATE_PAUSED -> {
                start()
                setImage!!.invoke(false)
            }
        }
    }

    override fun release() = mediaPlayer.release()
}