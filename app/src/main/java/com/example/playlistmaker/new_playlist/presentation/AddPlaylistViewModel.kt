package com.example.playlistmaker.new_playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.domain.repository.FileInteractor
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsInteractor
import com.example.playlistmaker.new_playlist.ui.models.PlaylistEditState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val updateState = MutableLiveData<PlaylistEditState>()
    fun observeEditState(): LiveData<PlaylistEditState> = updateState

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) { playlistsInteractor.insertPlaylist(playlist) }
    }

    fun updatePlaylist(playlist: Playlist) {
        updateState.postValue(PlaylistEditState.NowInEditing)
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(playlist).collectLatest {
                updateState.postValue(PlaylistEditState.EditFinished)
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