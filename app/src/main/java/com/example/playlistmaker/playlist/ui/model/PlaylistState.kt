package com.example.playlistmaker.playlist.ui.model

import com.example.playlistmaker.media_library.domain.models.Playlist

sealed interface PlaylistState {
    data class PlaylistReceived(val playlist: Playlist): PlaylistState
    data object PlaylistNotReceived: PlaylistState
}