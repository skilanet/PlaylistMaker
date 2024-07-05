package com.example.playlistmaker.new_playlist.domain.models

import com.example.playlistmaker.find.domain.models.Song

data class TracksInPlaylist(
    val tracks: ArrayList<Song>
)