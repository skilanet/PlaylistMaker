package com.example.playlistmaker.find.ui.states

import com.example.playlistmaker.find.domain.models.Song

sealed class HistoryState {
    data class Content(val data: ArrayList<Song>) : HistoryState()
    data object Empty: HistoryState()
}