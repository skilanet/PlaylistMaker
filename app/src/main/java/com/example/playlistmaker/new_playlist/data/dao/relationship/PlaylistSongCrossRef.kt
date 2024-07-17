package com.example.playlistmaker.new_playlist.data.dao.relationship

import androidx.room.Entity
import androidx.room.Index

@Entity(primaryKeys = ["playlistId", "trackId"], indices = [Index(value = ["trackId"])])
data class PlaylistSongCrossRef(
    val playlistId: Int,
    val trackId: Int
)