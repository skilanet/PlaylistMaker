package com.example.playlistmaker.playlist.domain.repository

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylistById(id: Int): Flow<Playlist?>
    suspend fun deleteTrackById(trackId: Int, playlistId: Int)
    fun getSongsInPlaylistByPlaylistId(id: Int): Flow<List<Song>>
    suspend fun deletePlaylistById(playlistId: Int)
}