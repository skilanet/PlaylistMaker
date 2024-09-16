package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.core.PlaylistConverter
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.data.dao.PlaylistsDatabase
import com.example.playlistmaker.new_playlist.data.dao.relationship.PlaylistSongCrossRef
import com.example.playlistmaker.playlist.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PlaylistRepositoryImpl(private val playlistsDatabase: PlaylistsDatabase) :
    PlaylistRepository {
    override fun getPlaylistById(id: Int): Flow<Playlist?> = flow {
        playlistsDatabase.getPlaylistDao().getPlaylistById(id).apply {
            val playlist = PlaylistConverter.fromEntitiesToModel(this)
            emit(playlist)
        }
    }

    override suspend fun deleteTrackById(trackId: Int, playlistId: Int) {
        playlistsDatabase.getPlaylistDao().deleteFromCrossRef(trackId, playlistId)
        val crossRef = getAllPlaylistsByTrackId(trackId)
        if (crossRef.isEmpty()) {
            playlistsDatabase.getPlaylistDao().deleteTrackById(trackId)
        }
    }

    override fun getSongsInPlaylistByPlaylistId(id: Int): Flow<List<Song>> = flow {
        val songs = playlistsDatabase.getPlaylistDao().getPlaylistById(id).songs
        emit(PlaylistConverter.fromEntitiesToModel(songs))
    }

    override suspend fun getAllPlaylistsByTrackId(trackId: Int): List<PlaylistSongCrossRef> {
        return playlistsDatabase.getPlaylistDao().getAllPlaylistsByTrackId(trackId)
    }

    override suspend fun deletePlaylistById(playlistId: Int) {
        val playlist = playlistsDatabase.getPlaylistDao().getPlaylistById(playlistId)
        playlist.songs?.let {
            it.map { songEntity ->
                deleteTrackById(
                    songEntity.trackId,
                    playlistId
                )
            }
        }
        playlistsDatabase.getPlaylistDao().deletePlaylistById(playlistId)
    }
}