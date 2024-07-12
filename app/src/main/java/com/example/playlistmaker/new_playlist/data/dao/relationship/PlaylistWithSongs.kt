package com.example.playlistmaker.new_playlist.data.dao.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.example.playlistmaker.new_playlist.data.dao.PlaylistEntity
import com.example.playlistmaker.new_playlist.data.dao.SongEntity

data class PlaylistWithSongs(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "playlistName"
    )
    val songs: List<SongEntity>?
)