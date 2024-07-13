package com.example.playlistmaker.new_playlist.data.dao.relationship

import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "trackId"])
data class PlaylistSongCrossRef(
    val playlistId: Int,
    val trackId: Int
)