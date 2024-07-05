package com.example.playlistmaker.media_library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.ui.models.PlaylistsState
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
): ViewModel() {

    private val playlistsState = MutableLiveData<PlaylistsState>()
    fun observePlaylistState(): LiveData<PlaylistsState> = playlistsState

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getAllPlaylists().collect {playlists ->
                renderPlaylists(playlists)
            }
        }
    }

    private fun renderPlaylists(playlists: List<Playlist>) {
        if (playlists.isEmpty()) playlistsState.postValue(PlaylistsState.Empty)
        else playlistsState.postValue(PlaylistsState.Content(playlists))
    }
}