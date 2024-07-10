package com.example.playlistmaker.new_playlist.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlaylistEntity::class],
    version = 2
)
abstract class PlaylistsDatabase: RoomDatabase() {
    abstract fun getPlaylistDao(): PlaylistDao
}