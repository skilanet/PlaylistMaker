package com.example.playlistmaker.media_library.data.impl

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.data.converter.Converter
import com.example.playlistmaker.media_library.data.dao.AppDatabase
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteSongsRepositoryImpl(private val appDatabase: AppDatabase): FavoriteSongsRepository {
    override fun favoriteSongs(): Flow<List<Song>> = flow {
        val movies = appDatabase.getDao().getFavoriteSongs()
        emit(Converter.fromEntitiesToModels(movies))
    }
}