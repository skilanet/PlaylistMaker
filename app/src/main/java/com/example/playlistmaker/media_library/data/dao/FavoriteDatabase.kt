package com.example.playlistmaker.media_library.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 2,
    entities = [SongEntity::class]
)
abstract class FavoriteDatabase: RoomDatabase() {
    abstract fun getSongDao(): SongsDao
}