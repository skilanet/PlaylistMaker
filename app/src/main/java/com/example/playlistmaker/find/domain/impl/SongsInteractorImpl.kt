package com.example.playlistmaker.find.domain.impl

import com.example.playlistmaker.find.domain.repository.SongsInteractor
import com.example.playlistmaker.find.domain.repository.SongRepository
import java.util.concurrent.Executors

class SongsInteractorImpl(private val repository: SongRepository): SongsInteractor {
    override fun searchSongs(term: String) {
        executor.execute {
            consumer.consume(repository.getSongs(term))
        }
    }

}