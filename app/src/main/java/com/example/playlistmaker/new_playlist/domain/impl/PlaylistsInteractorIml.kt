package com.example.playlistmaker.new_playlist.domain.impl

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsInteractor
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorIml(private val repository: PlaylistsRepository) : PlaylistsInteractor {
    override fun getAllPlaylists(): Flow<List<Playlist>> = repository.getAllPlaylists()
    override fun getPlaylistById(id: Int): Flow<Playlist?> = repository.getPlaylistById(id)

    override fun updatePlaylist(playlist: Playlist): Flow<Int> = repository.updatePlaylist(playlist)

    override suspend fun insertPlaylist(playlist: Playlist) = repository.insertPlaylist(playlist)

    override suspend fun insertSong(playlistId: Int, song: Song) =
        repository.insertSong(playlistId, song)

}