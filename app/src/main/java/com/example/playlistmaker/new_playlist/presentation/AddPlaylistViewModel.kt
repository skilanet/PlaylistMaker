package com.example.playlistmaker.new_playlist.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.domain.repository.FileInteractor
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val fileInteractor: FileInteractor
) : ViewModel() {

    private val _fileState = MutableStateFlow("")
    val fileState: StateFlow<String> = _fileState

    private val _playlistStateFlow = MutableStateFlow<Playlist?>(null)
    val playlistStateFlow: StateFlow<Playlist?> = _playlistStateFlow

    fun clickListener(playlist: Playlist, editState: Boolean) {
        viewModelScope.launch {
            Log.d("_TAG", "viewmodel: $playlist\n$editState")
            when (editState) {
                false -> playlistsInteractor.insertPlaylist(playlist)
                true -> playlistsInteractor.updatePlaylist(playlist)
            }
        }
    }

    fun getPlaylist(id: Int) {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(id).collect {
                _playlistStateFlow.value = it
            }
        }
    }

    fun putImageToLocalStorage(uri: String, playlistName: String) {
        viewModelScope.launch {
            fileInteractor.saveToLocalStorage(uri, playlistName)
            fileInteractor.getFromLocalStorage(playlistName).collectLatest {
                _fileState.value = it
            }
        }
    }
}