package com.example.playlistmaker.media_library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SongsDao {

    @Query("SELECT * FROM favorite_table ORDER BY id DESC")
    suspend fun getFavoriteSongs(): List<SongEntity>

    @Query("SELECT * FROM favorite_table WHERE trackId = :trackId")
    suspend fun getTrackByTrackId(trackId: Int): SongEntity?

    @Insert(entity = SongEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongIntoTable(songEntity: SongEntity)

    @Query("DELETE FROM favorite_table WHERE trackId = :trackId")
    suspend fun deleteSongByTrackId(trackId: Int)
}