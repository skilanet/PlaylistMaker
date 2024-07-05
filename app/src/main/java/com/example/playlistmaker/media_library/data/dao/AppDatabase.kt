package com.example.playlistmaker.media_library.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.new_playlist.data.dao.PlaylistDao
import com.example.playlistmaker.new_playlist.data.dao.PlaylistEntity

@Database(
    version = 4,
    entities = [SongEntity::class, PlaylistEntity::class]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getSongDao(): SongsDao
    abstract fun getPlaylistDao(): PlaylistDao
}