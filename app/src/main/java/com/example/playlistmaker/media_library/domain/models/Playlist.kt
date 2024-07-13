package com.example.playlistmaker.media_library.domain.models

import com.example.playlistmaker.find.domain.models.Song

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String?,
    val uri: String = "",
    val countOfTracks: Int,
    val tracks: List<Song>
)
