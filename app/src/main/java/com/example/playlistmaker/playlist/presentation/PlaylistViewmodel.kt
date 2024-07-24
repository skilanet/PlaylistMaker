package com.example.playlistmaker.playlist.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.playlist.domain.repository.PlaylistInteractor
import com.example.playlistmaker.playlist.ui.model.PlaylistState
import com.example.playlistmaker.playlist.ui.model.SongsInPlaylistState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private val playlistState = MutableLiveData<PlaylistState>()
    fun observePlaylistState(): LiveData<PlaylistState> = playlistState

    private val songsInPlaylistState = MutableLiveData<SongsInPlaylistState>()
    fun songsInPlaylistState(): LiveData<SongsInPlaylistState> = songsInPlaylistState

    private val aboutTracksInPlaylistState = MutableLiveData<Pair<Int, Int>>()
    fun observeAboutTracksInPlaylistState(): LiveData<Pair<Int, Int>> = aboutTracksInPlaylistState

    fun getPlaylist(id: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(id).collect {
                playlistState.value = renderState(it)
            }
        }
    }


    fun deleteTrackFromDB(trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackById(trackId, playlistId)
            playlistInteractor.getSongsInPlaylistByPlaylistId(playlistId).collect {
                renderState(it)
            }
        }
    }

    fun deletePlaylist(playlistId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            playlistInteractor.deletePlaylistById(playlistId)
        }
    }

    private fun renderState(playlist: Playlist?): PlaylistState {
        return if (playlist == null) PlaylistState.PlaylistNotReceived
        else {
            aboutTracksInPlaylistState.value =
                Pair(
                    calculateTime(playlist.tracks).also { Log.d("_TAG", it.toString()) },
                    playlist.countOfTracks
                )
            PlaylistState.PlaylistReceived(playlist)
        }
    }

    private fun calculateTime(array: List<Song>?): Int {
        val time = array?.let { _array -> _array.sumOf { it.trackTimeMillis } } ?: 0
        return SimpleDateFormat("mm", Locale.getDefault()).format(time).toInt()
    }

    private fun renderState(tracks: List<Song>) {
        songsInPlaylistState.value =
            if (tracks.isEmpty()) SongsInPlaylistState.Empty
            else SongsInPlaylistState.Content(tracks)
        aboutTracksInPlaylistState.value = Pair(calculateTime(tracks), tracks.size)
    }
}