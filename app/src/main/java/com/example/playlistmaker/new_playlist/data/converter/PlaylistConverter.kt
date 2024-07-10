package com.example.playlistmaker.new_playlist.data.converter

import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.data.dao.PlaylistEntity
import com.example.playlistmaker.new_playlist.domain.models.TracksInPlaylist
import com.google.gson.Gson

object PlaylistConverter {
    fun fromEntityToModel(entity: PlaylistEntity?): Playlist? = entity?.let {
        Playlist(
            name = entity.name,
            description = entity.description,
            uri = entity.uri,
            tracks = fromJson(entity.tracks),
            countOfTracks = entity.countOfTracks
        )
    }

    fun fromModelToEntity(model: Playlist): PlaylistEntity = PlaylistEntity(
        name = model.name,
        description = model.description,
        uri = model.uri,
        tracks = Gson().toJson(model.tracks),
        countOfTracks = model.tracks.tracks.size,
    )

    fun fromEntitiesToModels(entities: List<PlaylistEntity>?): List<Playlist> = entities?.let {
        it.mapNotNull { entity ->
            fromEntityToModel(entity)
        }
    } ?: emptyList()

    private fun fromJson(json: String): TracksInPlaylist {
        return if (json.isEmpty()) TracksInPlaylist(arrayListOf())
        else Gson().fromJson(json, TracksInPlaylist::class.java)
    }
}