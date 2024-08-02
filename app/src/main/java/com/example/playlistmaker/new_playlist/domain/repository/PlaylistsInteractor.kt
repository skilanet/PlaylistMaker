package com.example.playlistmaker.new_playlist.domain.repository

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: Int): Flow<Playlist?>
    fun updatePlaylist(playlist: Playlist): Flow<Int>
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun insertSong(playlistId: Int, song: Song)
}