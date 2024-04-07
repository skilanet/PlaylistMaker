package com.example.playlistmaker.media_player.domain.repository

interface MediaPlayerInteractor {

    var state: Int
    fun prepare()
    fun start()
    fun pause()
    fun release()
    fun getCurrentTime(): Int

}