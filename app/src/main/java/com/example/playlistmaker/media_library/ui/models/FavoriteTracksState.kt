package com.example.playlistmaker.media_library.ui.models

import com.example.playlistmaker.find.domain.models.Song

sealed class FavoriteTracksState(val data: Song? = null) {
    class Content(data: Song): FavoriteTracksState(data)
    data object Empty: FavoriteTracksState()
}