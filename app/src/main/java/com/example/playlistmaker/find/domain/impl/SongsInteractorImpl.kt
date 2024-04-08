package com.example.playlistmaker.find.domain.impl

import com.example.playlistmaker.find.domain.repository.SongsInteractor
import com.example.playlistmaker.find.domain.repository.SongRepository
import java.util.concurrent.Executors

class SongsInteractorImpl(private val repository: SongRepository): SongsInteractor {
    private val executor = Executors.newCachedThreadPool()
    override fun searchSongs(term: String, consumer: SongsInteractor.SongsConsumer) {
        executor.execute {
            consumer.consume(repository.getSongs(term))
        }
    }

}