package com.example.playlistmaker.new_playlist.data.dao.relationship

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.playlistmaker.new_playlist.data.dao.PlaylistEntity
import com.example.playlistmaker.new_playlist.data.dao.SongEntity

data class PlaylistWithSongs(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<SongEntity>?
)