package com.example.playlistmaker.find.domain.repository

import com.example.playlistmaker.find.domain.models.Resource
import com.example.playlistmaker.find.domain.models.Song

interface SongsInteractor {
    fun searchSongs(term: String, consumer: SongsConsumer)
    interface SongsConsumer{
        fun consume(foundSongs: Resource<List<Song>>)
    }
}