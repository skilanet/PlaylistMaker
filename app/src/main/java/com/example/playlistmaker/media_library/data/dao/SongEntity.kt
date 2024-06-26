package com.example.playlistmaker.media_library.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
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
    val addedTime: Long = System.currentTimeMillis()
)