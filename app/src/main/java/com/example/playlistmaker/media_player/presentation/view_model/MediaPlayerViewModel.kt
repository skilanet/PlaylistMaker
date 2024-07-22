package com.example.playlistmaker.media_player.presentation.view_model

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsInteractor
import com.example.playlistmaker.media_library.ui.models.PlaylistsState
import com.example.playlistmaker.media_player.ui.models.FavoriteState
import com.example.playlistmaker.media_player.ui.models.InPlaylistState
import com.example.playlistmaker.media_player.ui.models.PlayingState
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class MediaPlayerViewModel(
    private val song: Song,
    private val favoriteSongsInteractor: FavoriteSongsInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
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
    private fun initFavoriteState() {
        viewModelScope.launch {
            favoriteSongsInteractor.getTrackByTrackId(song.trackId).collect { song ->
                processResult(song)
            }
        }
    }

    fun insertSong(playlistId: Int, song: Song) = viewModelScope.launch {
        playlistsInteractor.insertSong(playlistId, song)
    }

    private val inPlaylistState = MutableLiveData<InPlaylistState>(InPlaylistState.NotInPlaylist)
    fun observeInPlaylistState(): LiveData<InPlaylistState> = inPlaylistState

    private val playlistsState = MutableLiveData<PlaylistsState>()
    fun observePlaylistsState(): LiveData<PlaylistsState> = playlistsState
    private fun updatePlaylistsState(newState: PlaylistsState) {
        playlistsState.postValue(newState)
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getAllPlaylists().collect { playlists ->
                renderPlaylists(playlists)
            }
        }
    }

    fun refreshPlaylists() {
        viewModelScope.launch {
            getPlaylists()
        }
    }

    private fun processResult(song: Song?) {
        if (song == null) favoriteState.value = FavoriteState.NotInFavorite
        else favoriteState.value = FavoriteState.InFavorite
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

    fun onPlayButtonClicked() {
        when (playingState.value) {
            is PlayingState.Playing -> onPause()
            is PlayingState.Prepared, is PlayingState.Paused -> onPlay()
            else -> {}
        }
    }

    fun onLikeClicked() {
        when (favoriteState.value) {
            is FavoriteState.InFavorite -> {
                viewModelScope.launch(Dispatchers.IO) {
                    favoriteSongsInteractor.deleteSongByTrackId(song.trackId)
                }
                favoriteState.value = FavoriteState.NotInFavorite
            }

            is FavoriteState.NotInFavorite -> {
                viewModelScope.launch(Dispatchers.IO) {
                    favoriteSongsInteractor.insertSong(song)
                }
                favoriteState.value = FavoriteState.InFavorite
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

    private fun renderPlaylists(playlists: List<Playlist>) {
        if (playlists.isEmpty()) updatePlaylistsState(PlaylistsState.Empty)
        else updatePlaylistsState(PlaylistsState.Content(playlists))
    }

    private fun getCurrentTime(): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            ?: "00:00"

    override fun onCleared() {
        super.onCleared()
        onRelease()
        Log.i("LIFECYCLE_TAG", "viewmodel: onCleared")
    }
}