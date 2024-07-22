package com.example.playlistmaker.new_playlist.data.impl

import android.util.Log
import com.example.playlistmaker.core.LogConstants
import com.example.playlistmaker.core.PlaylistConverter
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.data.dao.PlaylistsDatabase
import com.example.playlistmaker.new_playlist.data.dao.relationship.PlaylistSongCrossRef
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(private val playlistsDatabase: PlaylistsDatabase) :
    PlaylistsRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = playlistsDatabase.getPlaylistDao().getAllPlaylists().mapNotNull {
            val songs = playlistsDatabase.getPlaylistDao().getAllPlaylistsWithSongs(it.name)
            PlaylistConverter.fromEntitiesToModel(songs)
        }
        emit(playlists)
    }

    override fun getPlaylistById(id: Int): Flow<Playlist?> = flow {
        emit(
            PlaylistConverter.fromEntitiesToModel(
                playlistsDatabase.getPlaylistDao().getPlaylistById(id)
            )
        )
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        Log.d(LogConstants.UPDATE_TAG, "Repository $playlist")
        playlistsDatabase.getPlaylistDao().updatePlaylist(
            id = playlist.id,
            newName = playlist.name,
            newDescription = playlist.description ?: "",
            newUri = playlist.uri
        )
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        val entity = PlaylistConverter.fromModelToEntity(playlist)
        playlistsDatabase.getPlaylistDao().insertPlaylist(entity)
    }

    override suspend fun insertSong(playlistId: Int, song: Song) {
        val entity = PlaylistConverter.fromModelToEntity(song)
        with(playlistsDatabase.getPlaylistDao()) {
            insertSong(entity)
            insertPlaylistSongCrossRef(playlistId, song.trackId)
        }
    }

    override suspend fun insertPlaylistSongCrossRef(playlistId: Int, trackId: Int) {
        val crossRef = PlaylistSongCrossRef(playlistId, trackId)
        playlistsDatabase.getPlaylistDao().insertPlaylistSongCrossRef(crossRef)
    }
}