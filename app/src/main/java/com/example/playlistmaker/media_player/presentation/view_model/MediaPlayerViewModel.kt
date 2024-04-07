package com.example.playlistmaker.media_player.presentation.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor
import com.example.playlistmaker.media_player.ui.models.PlayingState
import com.example.playlistmaker.utils.Creator
import java.text.SimpleDateFormat
import java.util.Locale


class MediaPlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val application: Application
) : ViewModel() {
    companion object {
        private val HANDLER_TOKEN = Any()
        private const val DELAY = 300L
        fun getViewModelFactory(url: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    MediaPlayerViewModel(
                        mediaPlayerInteractor = Creator.provideMediaPlayerInteractor(
                            url = url
                        ), (this[APPLICATION_KEY] as App)
                    )
                }
            }
    }

    private val playingState = MutableLiveData<PlayingState>(PlayingState.Default)
    fun observePlayingState(): LiveData<PlayingState> = playingState

    fun onPrepare() {
        playingState.postValue(PlayingState.Prepared)
        mediaPlayerInteractor.prepare()
        timeState.postValue(application.getString(R.string.zero))
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
    private val timeState = MutableLiveData(application.getString(R.string.zero))
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