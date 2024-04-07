package com.example.playlistmaker.find.domain.impl

import com.example.playlistmaker.find.domain.repository.SongsUseCase
import com.example.playlistmaker.find.domain.repository.SongRepository
import java.util.concurrent.Executors

class SongsUseCaseImpl(private val repository: SongRepository): SongsUseCase {
    private val executor = Executors.newCachedThreadPool()
    override fun searchSongs(term: String, consumer: SongsUseCase.SongsConsumer) {
        executor.execute {
            consumer.consume(repository.getSongs(term))
        }
    }

}