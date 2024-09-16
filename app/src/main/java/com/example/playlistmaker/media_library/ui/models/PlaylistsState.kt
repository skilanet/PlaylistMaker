package com.example.playlistmaker.media_library.ui.models

import com.example.playlistmaker.media_library.domain.models.Playlist

sealed interface PlaylistsState {
    data class Content(val data: List<Playlist>): PlaylistsState
    data object Empty: PlaylistsState
}