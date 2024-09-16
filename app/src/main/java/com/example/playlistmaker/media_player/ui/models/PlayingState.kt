package com.example.playlistmaker.media_player.ui.models

import com.example.playlistmaker.R

sealed class PlayingState(
    val isButtonEnable: Boolean,
    val buttonBackground: Int,
    val currentTime: String
) {
    data object Default : PlayingState(false, R.drawable.play_image, "00:00")
    data object Prepared : PlayingState(true, R.drawable.play_image, "00:00")
    class Playing(currentTime: String) : PlayingState(true, R.drawable.pause_image, currentTime)
    class Paused(currentTime: String) : PlayingState(true, R.drawable.play_image, currentTime)
}