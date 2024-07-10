package com.example.playlistmaker.media_library.domain.models

import com.example.playlistmaker.new_playlist.domain.models.TracksInPlaylist

data class Playlist(
    val name: String,
    val description: String?,
    val uri: String = "",
    val tracks: TracksInPlaylist,
    val countOfTracks: Int,
)
