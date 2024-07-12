package com.example.playlistmaker.new_playlist.data.impl

import android.util.Log
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.data.converter.PlaylistConverter
import com.example.playlistmaker.new_playlist.data.dao.PlaylistsDatabase
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(private val playlistsDatabase: PlaylistsDatabase) :
    PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = playlistsDatabase.getPlaylistDao().getAllPlaylists().mapNotNull {
            val songs = playlistsDatabase.getPlaylistDao().getAllPlaylistsWithSongs(it.name)
            PlaylistConverter.fromEntitiesToModel(songs)
        }
        emit(playlists)
    }

    override fun getPlaylistByName(name: String): Flow<Playlist?> = flow {
        playlistsDatabase.getPlaylistDao().getAllPlaylistsWithSongs(name).apply {
            val playlist = PlaylistConverter.fromEntitiesToModel(this)
            emit(playlist)
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = PlaylistConverter.fromModelToEntity(playlist)
        playlistsDatabase.getPlaylistDao().updatePlaylist(
            name = entity.name,
            description = entity.description,
            uri = entity.uri
        )
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        val entity = PlaylistConverter.fromModelToEntity(playlist)
        playlistsDatabase.getPlaylistDao().insertPlaylist(entity)
    }

    override suspend fun insertSong(song: Song, playlistName: String) {
        val entity = PlaylistConverter.fromModelToEntity(song, playlistName)
        playlistsDatabase.getPlaylistDao().insertSong(entity)
    }
}