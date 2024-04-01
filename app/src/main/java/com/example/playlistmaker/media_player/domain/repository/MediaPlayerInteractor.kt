package com.example.playlistmaker.media_player.domain.repository

interface MediaPlayerInteractor {

    var currentTime: Int
    fun prepare()
    fun start()
    fun pause()
    fun stop()
    fun release()
    fun timeControl(): Runnable

}