package com.example.playlistmaker.new_playlist.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsInteractor
import com.example.playlistmaker.new_playlist.ui.models.PlaylistExistsState
import kotlinx.coroutines.launch

class AddPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val playlistState = MutableLiveData<PlaylistExistsState>(PlaylistExistsState.PlaylistNotExists)
    fun observePlaylistState(): LiveData<PlaylistExistsState> = playlistState

    fun clickListener(playlist: Playlist) {
        viewModelScope.launch {
            when (playlistState.value) {
                is PlaylistExistsState.PlaylistNotExists -> playlistsInteractor.insertPlaylist(playlist)
                is PlaylistExistsState.PlaylistExists -> playlistsInteractor.updatePlaylist(playlist)
                else -> {}
            }
        }
    }

    fun checkTrack(name: String) {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistByName(name).collect { playlist ->
                renderState(playlist)
                Log.d("PLAYLIST_EXIST_STATE", "$name ${playlistState.value} $playlist")
            }
        }
    }

    private fun renderState(playlist: Playlist?) {
        if (playlist == null) {
            Log.d("PLAYLIST_EXIST_STATE", "playlist = null")
            playlistState.value =PlaylistExistsState.PlaylistNotExists
        }
        else {
            Log.d("PLAYLIST_EXIST_STATE", "playlist != null")
            playlistState.value = PlaylistExistsState.PlaylistExists
        }
    }
}