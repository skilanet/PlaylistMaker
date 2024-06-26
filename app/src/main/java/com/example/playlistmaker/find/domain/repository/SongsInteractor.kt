package com.example.playlistmaker.find.domain.repository

import com.example.playlistmaker.find.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface SongsInteractor {
    suspend fun searchSongs(term: String): Flow<Pair<List<Song>?, Int>>
}