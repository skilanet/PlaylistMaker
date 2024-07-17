package com.example.playlistmaker.new_playlist.data.converter

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.data.dao.PlaylistEntity
import com.example.playlistmaker.new_playlist.data.dao.SongEntity
import com.example.playlistmaker.new_playlist.data.dao.relationship.PlaylistWithSongs

object PlaylistConverter {
    fun fromEntitiesToModel(playlistWithSongs: PlaylistWithSongs?) = playlistWithSongs?.let {
        with(it) {
            Playlist(
                id = playlist.playlistId,
                name = playlist.name,
                description = playlist.description,
                uri = playlist.uri,
                tracks = fromEntitiesToModel(songs),
                countOfTracks = songs?.size ?: 0
            )
        }
    }

    private fun fromEntitiesToModel(songEntities: List<SongEntity>?): List<Song> = songEntities?.let {
        it.map { entity -> fromEntityToModel(entity) }
    } ?: emptyList()

    fun fromModelToEntity(model: Playlist) = with(model) {
        PlaylistEntity(
            name = name,
            description = description,
            uri = uri
        )
    }

    private fun fromEntityToModel(entity: SongEntity) = with(entity) {
        Song(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            artworkUrl512 = artworkUrl512,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            country = country,
            primaryGenreName = primaryGenreName,
            previewUrl = previewUrl,
            currentTime = currentTime,
            currentDate = currentDate
        )
    }

    fun fromModelToEntity(model: Song) = with(model) {
        SongEntity(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            artworkUrl512 = artworkUrl512,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            country = country,
            primaryGenreName = primaryGenreName,
            previewUrl = previewUrl,
            currentTime = currentTime,
            currentDate = currentDate,
        )
    }
}