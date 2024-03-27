package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Song

interface SongsUseCase {
    fun searchSongs(term: String, consumer: SongsConsumer)
    interface SongsConsumer{
        fun consume(foundSongs: List<Song>)
    }
}