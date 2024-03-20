package com.example.playlistmaker.domain.models

data class Song(
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String = "",
    val artworkUrl512: String = "",
    val collectionName: String,
    val country: String,
    val primaryGenreName: String,
    val previewUrl: String,
    val currentTime: String,
    val currentDate: String
)
