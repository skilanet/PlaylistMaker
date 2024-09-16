package com.example.playlistmaker.playlist.domain.repository

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.data.dao.relationship.PlaylistSongCrossRef
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylistById(id: Int): Flow<Playlist?>
    suspend fun deleteTrackById(trackId: Int, playlistId: Int)
    fun getSongsInPlaylistByPlaylistId(id: Int): Flow<List<Song>>
    suspend fun getAllPlaylistsByTrackId(trackId: Int): List<PlaylistSongCrossRef>
    suspend fun deletePlaylistById(playlistId: Int)
}