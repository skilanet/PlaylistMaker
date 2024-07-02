package com.example.playlistmaker.media_library.ui.models

import com.example.playlistmaker.find.domain.models.Song

sealed class FavoriteTracksState(val data: List<Song>? = null) {
    class Content(data: List<Song>): FavoriteTracksState(data)
    data object Empty: FavoriteTracksState()
}