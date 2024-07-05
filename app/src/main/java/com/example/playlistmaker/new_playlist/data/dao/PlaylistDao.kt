package com.example.playlistmaker.new_playlist.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(entity: PlaylistEntity)

    @Query("UPDATE playlists_table SET " +
            "description = :description, " +
            "uri = :uri, " +
            "tracks = :tracks, " +
            "countOfTracks = :countOfTracks " +
            "WHERE name = :name")
    suspend fun updatePlaylist(
        name: String,
        description: String?,
        uri: String,
        tracks: String,
        countOfTracks: Int
    )

    @Query("SELECT * FROM playlists_table WHERE name = :name")
    suspend fun getPlaylistByName(name: String): PlaylistEntity

    @Query("SELECT * FROM playlists_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>
}