package com.example.playlistmaker.domain.repository

interface MediaPlayerInteractor {

    var setString: ((String) -> Unit)?
    var setImage: ((Boolean) -> Unit)?
    fun prepare()
    fun start()
    fun pause()
    fun stop()
    fun playBackControl()
    fun release()
    fun timeControl(): Runnable

}