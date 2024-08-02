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

    @Query("UPDATE playlists_table SET name = :newName, " +
            "description = :newDescription, " +
            "uri = :newUri WHERE playlistId = :currentId")
    suspend fun updatePlaylist(currentId: Int, newName: String, newDescription: String, newUri: String): Int

    @Insert(entity = PlaylistSongCrossRef::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSongCrossRef(crossRef: PlaylistSongCrossRef)

    @Query("SELECT * FROM playlists_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Transaction
    @Query("SELECT * FROM playlists_table WHERE name = :newName")
    suspend fun getAllPlaylistsWithSongs(newName: String): PlaylistWithSongs?

    @Transaction
    @Query("SELECT * FROM playlists_table WHERE playlistId = :id")
    suspend fun getPlaylistById(id: Int): PlaylistWithSongs

    @Query("DELETE FROM playlists_songs WHERE trackId = :id")
    suspend fun deleteTrackById(id: Int)

    @Query("DELETE FROM PlaylistSongCrossRef WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun deleteFromCrossRef(trackId: Int, playlistId: Int)

    @Query("SELECT * FROM PlaylistSongCrossRef WHERE trackId = :trackId")
    suspend fun getAllPlaylistsByTrackId(trackId: Int): List<PlaylistSongCrossRef>

    @Query("DELETE FROM playlists_table WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Int)
}