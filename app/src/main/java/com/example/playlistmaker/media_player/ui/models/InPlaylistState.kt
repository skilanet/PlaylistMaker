package com.example.playlistmaker.media_player.ui.models

sealed interface InPlaylistState {
    data object InPlaylist: InPlaylistState
    data object NotInPlaylist: InPlaylistState
}