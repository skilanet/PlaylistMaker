package com.example.playlistmaker.media_player.view_model

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor
import com.example.playlistmaker.utils.Creator

class MediaPlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
) : ViewModel() {
    companion object {
        fun getViewModelFactory(url: String, handler: Handler): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    MediaPlayerViewModel(
                        mediaPlayerInteractor = Creator.provideMediaPlayerInteractor(
                            url = url, handler = handler
                        )
                    )
                }
            }
    }

    private val playerState = MutableLiveData<PlayingState>(PlayingState.Default)
    private fun updateState(state: PlayingState){
        playerState.postValue(state)
    }
    fun observePlayerState(): LiveData<PlayingState> = playerState
    fun mediaPlayer(state: PlayingState){
        mediaPlayerInteractor.timeControl()
        when (state){
            is PlayingState.Default -> {
                mediaPlayerInteractor.prepare()
                updateState(PlayingState.Prepared)
            }
            is PlayingState.Prepared, PlayingState.Paused -> {
                mediaPlayerInteractor.start()
                updateState(PlayingState.Playing)
            }
            is PlayingState.Playing -> {
                mediaPlayerInteractor.pause()
                updateState(PlayingState.Paused)
            }

        }
    }

    private val timeState = MutableLiveData(mediaPlayerInteractor.currentTime)
    private fun updateTime(){
        timeState.postValue(mediaPlayerInteractor.currentTime)
    }
    fun observeTimeState(): LiveData<Int> = timeState


}