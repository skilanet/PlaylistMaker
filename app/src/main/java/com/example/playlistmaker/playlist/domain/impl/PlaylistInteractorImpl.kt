package com.example.playlistmaker.playlist.domain.impl

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.playlist.domain.repository.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {
    override fun getPlaylistById(id: Int): Flow<Playlist?> = repository.getPlaylistById(id)

    override suspend fun deleteTrackById(trackId: Int, playlistId: Int) = repository.deleteTrackById(
            trackId, playlistId
        )

    override fun getSongsInPlaylistByPlaylistId(id: Int): Flow<List<Song>> =
        repository.getSongsInPlaylistByPlaylistId(id)

    override suspend fun deletePlaylistById(playlistId: Int) = repository.deletePlaylistById(playlistId)
}