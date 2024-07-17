package com.example.playlistmaker.new_playlist.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.new_playlist.data.dao.relationship.PlaylistSongCrossRef
import com.example.playlistmaker.new_playlist.data.dao.relationship.PlaylistWithSongs

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Insert(entity = SongEntity::class, OnConflictStrategy.REPLACE)
    suspend fun insertSong(songEntity: SongEntity)

    @Query("UPDATE playlists_table SET " +
            "description = :description, " +
            "uri = :uri " +
            "WHERE name = :name")
    suspend fun updatePlaylist(
        name: String,
        description: String?,
        uri: String,
    )

    @Insert(entity = PlaylistSongCrossRef::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSongCrossRef(crossRef: PlaylistSongCrossRef)

    @Transaction
    @Query("SELECT * FROM playlists_table WHERE name = :name")
    suspend fun getPlaylistByName(name: String): PlaylistWithSongs

    @Query("SELECT * FROM playlists_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Transaction
    @Query("SELECT * FROM playlists_table WHERE name = :newName")
    suspend fun getAllPlaylistsWithSongs(newName: String): PlaylistWithSongs?
}