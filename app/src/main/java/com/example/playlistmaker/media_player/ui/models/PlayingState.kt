package com.example.playlistmaker.media_player.ui.models

sealed class PlayingState {
    data object Default: PlayingState()
    data object Prepared: PlayingState()
    data object Playing: PlayingState()
    data object Paused: PlayingState()
    data object Complete: PlayingState()
}