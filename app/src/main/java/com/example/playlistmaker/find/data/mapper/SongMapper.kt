package com.example.playlistmaker.find.data.mapper

import com.example.playlistmaker.find.data.dto.SongDto
import com.example.playlistmaker.find.domain.models.Song

object SongMapper {

    fun map(dto: List<SongDto>?): List<Song> = dto?.map { song ->
        Song(
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
    } ?: emptyList()
}