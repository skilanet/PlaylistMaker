package com.example.playlistmaker.new_playlist.ui.models

sealed interface PlaylistExistsState {
    data object PlaylistExists: PlaylistExistsState
    data object PlaylistNotExists: PlaylistExistsState
}