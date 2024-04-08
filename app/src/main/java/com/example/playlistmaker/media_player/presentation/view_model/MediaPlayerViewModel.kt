package com.example.playlistmaker.media_player.presentation.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor
import com.example.playlistmaker.media_player.ui.models.PlayingState
import java.text.SimpleDateFormat
import java.util.Locale


class MediaPlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val applicationContext: Context
) : ViewModel() {
    companion object {
        private val HANDLER_TOKEN = Any()
        private const val DELAY = 300L
    }

    private val playingState = MutableLiveData<PlayingState>(PlayingState.Default)
    fun observePlayingState(): LiveData<PlayingState> = playingState

    fun onPrepare() {
        playingState.postValue(PlayingState.Prepared)
        mediaPlayerInteractor.prepare()
        timeState.postValue(applicationContext.getString(R.string.zero))
    }

    private fun onPlay() {
        playingState.postValue(PlayingState.Playing)
        mediaPlayerInteractor.start()
    }

    private fun onPause() {
        playingState.postValue(PlayingState.Paused)
        mediaPlayerInteractor.pause()
    }

    fun stateControl() {
        val intState = mediaPlayerInteractor.state
        when (intState) {
            0x000000 -> playingState.postValue(PlayingState.Default)
            0x000001 -> playingState.postValue(PlayingState.Prepared)
            0x000002 -> playingState.postValue(PlayingState.Playing)
            0x000003 -> playingState.postValue(PlayingState.Paused)
        }
    }

    fun playingControl() {
        if (playingState.value is PlayingState.Playing) onPause()
        else onPlay()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val timeState = MutableLiveData(applicationContext.getString(R.string.zero))
    fun observeTimeState(): LiveData<String> = timeState
    fun updateTimeState() {
        var mpCurrentTime = mediaPlayerInteractor.getCurrentTime()
        handler.postAtTime(
            { mpCurrentTime = mediaPlayerInteractor.getCurrentTime() },
            HANDLER_TOKEN,
            SystemClock.uptimeMillis() + DELAY
        )
        val currentTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
            mpCurrentTime
        )
        timeState.postValue(currentTime)
    }


    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(HANDLER_TOKEN)
        mediaPlayerInteractor.release()
    }
}