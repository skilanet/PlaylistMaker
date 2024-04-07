package com.example.playlistmaker.find.data.dto

import java.text.SimpleDateFormat
import java.util.Locale

data class SongDto(
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String,
    val collectionName: String,
    val country: String,
    val primaryGenreName: String,
    val previewUrl: String,
    val trackTimeMillis: Int,
    val releaseDate: String
) {
    val artworkUrl512: String
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    val currentTime: String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    val currentDate: String
        get() {
            val day = releaseDate.substring(8, 10)
            val month = releaseDate.substring(5, 7)
            val year = releaseDate.substring(0, 4)
            return StringBuilder("$day.$month.$year").toString()
        }
}