package com.example.playlistmaker.media_library.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [SongEntity::class]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getDao(): SongsDao
}