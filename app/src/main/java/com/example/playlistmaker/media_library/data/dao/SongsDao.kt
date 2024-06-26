package com.example.playlistmaker.media_library.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.concurrent.Flow

@Dao
interface SongsDao {

    @Insert(entity = SongEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongIntoTable(songEntity: SongEntity)

    @Query("SELECT * FROM favorite_table ORDER BY addedTime DESC")
    suspend fun getFavoriteSongs(): List<SongEntity>

    @Delete(entity = SongEntity::class)
    suspend fun deleteSong()

    @Query("DELETE FROM favorite_table WHERE id = :songId")
    suspend fun deleteSongById(songId: Int)
}