package com.example.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.playlist.domain.repository.PlaylistInteractor
import com.example.playlistmaker.playlist.ui.model.SongsInPlaylistState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewmodel(
    private val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    init {
        getPlaylist(playlistId)
    }

    private val _playlistState: MutableStateFlow<Pair<Playlist, Int>?> = MutableStateFlow(null)
    val playlistState: StateFlow<Pair<Playlist, Int>?> = _playlistState

    private val songsInPlaylistState = MutableLiveData<SongsInPlaylistState>()
    fun songsInPlaylistState(): LiveData<SongsInPlaylistState> = songsInPlaylistState

    private fun getPlaylist(id: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(id).collect {
                _playlistState.value = renderState(it)
            }
        }
    }

    fun deleteTrackFromDB(trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackById(trackId, playlistId)
            playlistInteractor.getSongsInPlaylistByPlaylistId(playlistId).collect {
                renderFlow(it)
            }
        }
    }

    private fun renderState(playlist: Playlist?): Pair<Playlist, Int>? {
        return if (playlist == null) null
        else Pair(playlist, calculateTime(playlist.tracks))
    }

    private fun calculateTime(array: List<Song>?): Int {
        val time = array?.let { _array -> _array.sumOf { it.trackTimeMillis } } ?: 0
        return SimpleDateFormat("mm", Locale.getDefault()).format(time).toInt()
    }

    private fun renderFlow(tracks: List<Song>) {
        songsInPlaylistState.postValue(
            if (tracks.isEmpty()) SongsInPlaylistState.Empty
            else SongsInPlaylistState.Content(tracks)
        )

    }
}