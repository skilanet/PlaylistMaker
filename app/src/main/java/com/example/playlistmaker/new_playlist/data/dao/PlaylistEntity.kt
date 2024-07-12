package com.example.playlistmaker.new_playlist.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String?,
    val uri: String,
)