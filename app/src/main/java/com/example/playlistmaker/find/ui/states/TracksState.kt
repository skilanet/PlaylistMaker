package com.example.playlistmaker.find.ui.states

import com.example.playlistmaker.find.domain.models.Song

sealed class TracksState {
    data object Loading : TracksState()
    data class Content(val tracks: ArrayList<Song>) : TracksState()
    data object Error : TracksState()
    data object NothingNotFound : TracksState()
}