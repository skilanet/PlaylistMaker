package com.example.playlistmaker.media_player.presentation.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsInteractor
import com.example.playlistmaker.media_player.ui.models.FavoriteState
import com.example.playlistmaker.media_player.ui.models.PlayingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class MediaPlayerViewModel(
    private val song: Song,
    private val favoriteSongsInteractor: FavoriteSongsInteractor,
    private val mediaPlayer: MediaPlayer,
) : ViewModel() {

    private val playingState = MutableLiveData<PlayingState>(PlayingState.Default)
    fun observePlayingState(): LiveData<PlayingState> = playingState
    private var timerJob: Job? = null

    init {
        initMediaPlayer()
        initFavoriteState()
    }

    private val favoriteState = MutableLiveData<FavoriteState>()
    fun observeFavoriteState(): LiveData<FavoriteState> = favoriteState
    private fun updateFavoriteState(newState: FavoriteState) = favoriteState.postValue(newState)
    private fun initFavoriteState() {
        viewModelScope.launch {
            favoriteSongsInteractor.getTrackByTrackId(song.trackId).collect { song ->
                processResult(song)
            }
        }
    }

    private fun processResult(song: Song?) {
        if (song == null) updateFavoriteState(FavoriteState.NotInFavorite)
        else updateFavoriteState(FavoriteState.InFavorite)
    }

    private fun initMediaPlayer() {
        mediaPlayer.setDataSource(song.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playingState.postValue(PlayingState.Prepared)
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playingState.postValue(PlayingState.Prepared)
        }
    }

    fun onButtonClicked() {
        when (playingState.value) {
            is PlayingState.Playing -> onPause()
            is PlayingState.Prepared, is PlayingState.Paused -> onPlay()
            else -> {}
        }
    }

    fun onLikeClicked(){
        when (favoriteState.value){
            is FavoriteState.InFavorite -> {
                viewModelScope.launch(Dispatchers.IO) {
                    favoriteSongsInteractor.deleteSongByTrackId(song.trackId)
                }
                updateFavoriteState(FavoriteState.NotInFavorite)
            }
            is FavoriteState.NotInFavorite -> {
                viewModelScope.launch(Dispatchers.IO) {
                    favoriteSongsInteractor.insertSong(song)
                }
                updateFavoriteState(FavoriteState.InFavorite)
            }
            else -> {}
        }
    }

    private fun onPlay() {
        mediaPlayer.start()
        playingState.postValue(PlayingState.Playing(getCurrentTime()))
        startTimer()
    }

    fun onPause() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playingState.postValue(PlayingState.Paused(getCurrentTime()))
    }

    private fun onRelease() {
        mediaPlayer.stop()
        mediaPlayer.release()
        playingState.postValue(PlayingState.Default)
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(300L)
                playingState.postValue(PlayingState.Playing(getCurrentTime()))
            }
        }
    }

    private fun getCurrentTime(): String = SimpleDateFormat("mm:ss", Locale.getDefault())
        .format(mediaPlayer.currentPosition) ?: "00:00"

    override fun onCleared() {
        super.onCleared()
        onRelease()
    }
}