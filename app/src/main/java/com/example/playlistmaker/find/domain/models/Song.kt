package com.example.playlistmaker.find.domain.models

data class Song(
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
