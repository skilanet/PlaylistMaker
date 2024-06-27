package com.example.playlistmaker.media_library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsInteractor
import com.example.playlistmaker.media_library.ui.models.FavoriteTracksState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteSongsInteractor: FavoriteSongsInteractor
): ViewModel() {

    private val viewState = MutableLiveData<FavoriteTracksState>()
    fun observeViewState(): LiveData<FavoriteTracksState> = viewState
    private fun updateViewState(newState: FavoriteTracksState) = viewState.postValue(newState)

    fun makeQueryToDB(){
        viewModelScope.launch {
            favoriteSongsInteractor.favoriteSongs().collect { songs ->
                renderQuery(songs)
            }
        }
    }

    private fun renderQuery(songs: List<Song>?) {
        if (songs.isNullOrEmpty()) updateViewState(FavoriteTracksState.Empty)
        else updateViewState(FavoriteTracksState.Content(songs))
    }
}