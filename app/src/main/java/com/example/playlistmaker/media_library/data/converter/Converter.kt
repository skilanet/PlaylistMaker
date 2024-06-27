package com.example.playlistmaker.media_library.data.converter

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.data.dao.SongEntity

object Converter {

    fun fromEntitiesToModels(entities: List<SongEntity>?): List<Song> = entities?.let {
        it.map { entity ->
            Song(
                trackId = entity.trackId,
                trackName = entity.trackName,
                artistName = entity.artistName,
                artworkUrl512 = entity.artworkUrl512,
                artworkUrl100 = entity.artworkUrl100,
                collectionName = entity.collectionName,
                country = entity.country,
                primaryGenreName = entity.primaryGenreName,
                previewUrl = entity.previewUrl,
                currentTime = entity.currentTime,
                currentDate = entity.currentDate
            )
        }
    } ?: emptyList()

    fun fromModelToEntity(song: Song): SongEntity = SongEntity(
        trackId = song.trackId,
        trackName = song.trackName,
        artistName = song.artistName,
        artworkUrl512 = song.artworkUrl512,
        artworkUrl100 = song.artworkUrl100,
        collectionName = song.collectionName,
        country = song.country,
        primaryGenreName = song.primaryGenreName,
        previewUrl = song.previewUrl,
        currentTime = song.currentTime,
        currentDate = song.currentDate
    )

    fun fromEntityToModel(entity: SongEntity?): Song? {
        return if (entity != null) Song(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            artworkUrl512 = entity.artworkUrl512,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            country = entity.country,
            primaryGenreName = entity.primaryGenreName,
            previewUrl = entity.previewUrl,
            currentTime = entity.currentTime,
            currentDate = entity.currentDate
        )
        else null
    }
}