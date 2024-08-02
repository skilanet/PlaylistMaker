package com.example.playlistmaker.playlist.ui.model

import com.example.playlistmaker.find.domain.models.Song

sealed interface SongsInPlaylistState {
    data class Content(val data: List<Song>) : SongsInPlaylistState
    data object Empty: SongsInPlaylistState
}