package com.example.playlistmaker.media_library.domain.models

import android.net.Uri
import com.example.playlistmaker.new_playlist.domain.models.TracksInPlaylist

data class Playlist(
    var name: String,
    var description: String?,
    var uri: Uri = Uri.EMPTY,
    var tracks: TracksInPlaylist,
    var countOfTracks: Int,
)
