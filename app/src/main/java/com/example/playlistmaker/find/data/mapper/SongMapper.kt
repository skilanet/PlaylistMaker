package com.example.playlistmaker.find.data.mapper

import com.example.playlistmaker.find.data.dto.SongDto
import com.example.playlistmaker.find.domain.models.Song

object SongMapper {

    fun map(dto: SongDto): Song = Song(
        trackName = dto.trackName,
        artistName = dto.artistName,
        artworkUrl100 = dto.artworkUrl100,
        artworkUrl512 = dto.artworkUrl512,
        collectionName = dto.collectionName,
        country = dto.country,
        primaryGenreName = dto.primaryGenreName,
        previewUrl = dto.previewUrl,
        currentTime = dto.currentTime,
        currentDate = dto.currentDate
    )
}