package com.example.playlistmaker.new_playlist.domain.repository

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistByName(name: String): Flow<Playlist?>
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun insertSong(playlistId: Int, song: Song)
    suspend fun insertPlaylistSongCrossRef(playlistId: Int, trackId: Int)
}