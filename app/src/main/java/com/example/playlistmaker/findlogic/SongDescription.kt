package com.example.playlistmaker.findlogic

import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Locale

class SongDescription(
    val trackName: String,
    val artistName: String,
    val artworkUrl100: String,
    val collectionName: String,
    val country: String,
    val primaryGenreName: String
) {
    val artworkUrl512: String
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}

fun SongDescription.fill(tvTrackName: TextView, tvAuthor: TextView, tvTrackTime: TextView) {
    tvTrackName.text = trackName
    tvAuthor.text = artistName
    tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L)
}


