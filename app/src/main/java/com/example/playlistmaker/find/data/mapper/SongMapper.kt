package com.example.playlistmaker.find.data.mapper

import com.example.playlistmaker.find.data.dto.SongDto
import com.example.playlistmaker.find.domain.models.Song

object SongMapper {

    fun map(dto: List<SongDto>?): List<Song> = dto?.map { song ->
        with(song) {
            Song(
                trackId = song.trackId,
                trackName = trackName ?: "",
                artistName = artistName ?: "",
                artworkUrl512 = artworkUrl512,
                artworkUrl100 = artworkUrl100 ?: "",
                collectionName = collectionName ?: "",
                country = country ?: "",
                primaryGenreName = primaryGenreName ?: "",
                previewUrl = previewUrl ?: "",
                currentTime = song.currentTime,
                currentDate = song.currentDate
            )
        }
    } ?: emptyList()
}