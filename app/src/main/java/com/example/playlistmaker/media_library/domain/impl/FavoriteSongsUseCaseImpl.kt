package com.example.playlistmaker.media_library.domain.impl

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsRepository
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsUseCase
import kotlinx.coroutines.flow.Flow

class FavoriteSongsUseCaseImpl(
    private val favoriteSongsRepository: FavoriteSongsRepository
) : FavoriteSongsUseCase {
    override fun favoriteSongs(): Flow<List<Song>> =
        favoriteSongsRepository.favoriteSongs()
}