package com.example.playlistmaker.new_playlist.data.impl

import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.data.converter.PlaylistConverter
import com.example.playlistmaker.new_playlist.data.dao.PlaylistsDatabase
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(private val playlistsDatabase: PlaylistsDatabase) :
    PlaylistRepository {
    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val entities = playlistsDatabase.getPlaylistDao().getAllPlaylists()
        emit(PlaylistConverter.fromEntitiesToModels(entities))
    }

    override fun getPlaylistByName(name: String): Flow<Playlist?> = flow {
        val entity = playlistsDatabase.getPlaylistDao().getPlaylistByName(name)
        emit(PlaylistConverter.fromEntityToModel(entity))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = PlaylistConverter.fromModelToEntity(playlist)
        playlistsDatabase.getPlaylistDao().updatePlaylist(
            name = entity.name,
            description = entity.description,
            uri = entity.uri,
            tracks = entity.tracks,
            countOfTracks = entity.countOfTracks
        )
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        val entity = PlaylistConverter.fromModelToEntity(playlist)
        playlistsDatabase.getPlaylistDao().insertPlaylist(entity)
    }
}