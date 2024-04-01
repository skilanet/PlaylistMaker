package com.example.playlistmaker.find.view_model

sealed class HistoryState {
    data class Content(val data: List<String>) : HistoryState()
    data object Empty: HistoryState()
}