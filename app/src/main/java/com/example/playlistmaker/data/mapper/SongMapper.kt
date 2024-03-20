package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.SongDto
import com.example.playlistmaker.domain.models.Song

object SongMapper {

    fun map(dto: SongDto): Song = Song(
        trackName = dto.trackName,
        artistName = dto.artistName,
        artworkUrl512 = dto.artworkUrl512,
        collectionName = dto.collectionName,
        country = dto.country,
        primaryGenreName = dto.primaryGenreName,
        previewUrl = dto.previewUrl,
        currentTime = dto.currentTime,
        currentDate = dto.currentDate
    )
}