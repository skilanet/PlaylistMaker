package com.example.playlistmaker.new_playlist.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.new_playlist.data.dao.relationship.PlaylistSongCrossRef

@Database(
    entities = [PlaylistEntity::class, SongEntity::class, PlaylistSongCrossRef::class],
    version = 5
)
abstract class PlaylistsDatabase: RoomDatabase() {
    abstract fun getPlaylistDao(): PlaylistDao
}