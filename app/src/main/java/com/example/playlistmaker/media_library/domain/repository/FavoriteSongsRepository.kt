package com.example.playlistmaker.media_library.domain.repository

import com.example.playlistmaker.find.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface FavoriteSongsRepository {
    fun favoriteSongs(): Flow<List<Song>>
    fun getTrackByTrackId(trackId: Int): Flow<Song?>
    suspend fun insertSong(song: Song)
    suspend fun deleteSongByTrackId(trackId: Int)
}