package com.example.playlistmaker.new_playlist.ui.models

sealed interface PlaylistEditState {
    data object NowInEditing: PlaylistEditState
    data object EditFinished: PlaylistEditState
}