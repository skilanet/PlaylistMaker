package com.example.playlistmaker.new_playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.domain.repository.FileInteractor
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsInteractor
import com.example.playlistmaker.new_playlist.ui.models.PlaylistExistsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val fileInteractor: FileInteractor
) : ViewModel() {

    private val playlistState =
        MutableLiveData<PlaylistExistsState>(PlaylistExistsState.PlaylistNotExists)
    private val _fileState = MutableStateFlow("")

    fun observePlaylistState(): LiveData<PlaylistExistsState> = playlistState
    val fileState: StateFlow<String> = _fileState

    fun clickListener(playlist: Playlist) {
        viewModelScope.launch {
            when (playlistState.value) {
                is PlaylistExistsState.PlaylistNotExists -> playlistsInteractor.insertPlaylist(
                    playlist
                )

                is PlaylistExistsState.PlaylistExists -> playlistsInteractor.updatePlaylist(playlist)
                else -> {}
            }
        }
    }

    fun checkTrack(name: String) {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistByName(name).collect { playlist ->
                renderState(playlist)
            }
        }
    }

    private fun renderState(playlist: Playlist?) {
        if (playlist == null) {
            playlistState.value = PlaylistExistsState.PlaylistNotExists
        } else {
            playlistState.value = PlaylistExistsState.PlaylistExists
        }
    }

    fun putImageToLocalStorage(uri: String, playlistName: String) {
        viewModelScope.launch {
            fileInteractor.saveToLocalStorage(uri, playlistName)
            viewModelScope.launch {
                fileInteractor.getFromLocalStorage(playlistName)
            }
        }
    }
}