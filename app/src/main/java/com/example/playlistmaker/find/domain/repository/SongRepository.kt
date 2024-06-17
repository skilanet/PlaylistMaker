package com.example.playlistmaker.find.domain.repository

import com.example.playlistmaker.find.domain.models.Resource
import com.example.playlistmaker.find.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    suspend fun getSongs(term: String): Flow<Resource<List<Song>>>
}