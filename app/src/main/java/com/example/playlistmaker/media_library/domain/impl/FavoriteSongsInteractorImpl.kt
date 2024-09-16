package com.example.playlistmaker.media_library.domain.impl

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsInteractor
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsRepository
import kotlinx.coroutines.flow.Flow

class FavoriteSongsInteractorImpl(
    private val favoriteSongsRepository: FavoriteSongsRepository
) : FavoriteSongsInteractor {
    override fun favoriteSongs(): Flow<List<Song>> =
        favoriteSongsRepository.favoriteSongs()

    override fun getTrackByTrackId(trackId: Int): Flow<Song?> =
        favoriteSongsRepository.getTrackByTrackId(trackId)

    override suspend fun insertSong(song: Song) {
        favoriteSongsRepository.insertSong(song)
    }

    override suspend fun deleteSongByTrackId(trackId: Int) {
        favoriteSongsRepository.deleteSongByTrackId(trackId)
    }
}