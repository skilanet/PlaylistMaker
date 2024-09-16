package com.example.playlistmaker.new_playlist.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = false)
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val artworkUrl512: String = "",
    val artworkUrl100: String = "",
    val collectionName: String,
    val country: String,
    val primaryGenreName: String,
    val previewUrl: String,
    val currentTime: String,
    val currentDate: String,
    val trackTimeMillis: Int,
)