package com.example.playlistmaker.find.domain.repository

import com.example.playlistmaker.find.domain.models.Resource
import com.example.playlistmaker.find.domain.models.Song

interface SongsUseCase {
    fun searchSongs(term: String, consumer: SongsConsumer)
    interface SongsConsumer{
        fun consume(foundSongs: Resource<List<Song>>)
    }
}