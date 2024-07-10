package com.example.playlistmaker.media_library.data.impl

import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_library.data.converter.Converter
import com.example.playlistmaker.media_library.data.dao.FavoriteDatabase
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteSongsRepositoryImpl(private val favoriteDatabase: FavoriteDatabase): FavoriteSongsRepository {
    override fun favoriteSongs(): Flow<List<Song>> = flow {
        val movies = favoriteDatabase.getSongDao().getFavoriteSongs()
        emit(Converter.fromEntitiesToModels(movies))
    }

    override fun getTrackByTrackId(trackId: Int): Flow<Song?> = flow {
        val track = favoriteDatabase.getSongDao().getTrackByTrackId(trackId)
        emit(Converter.fromEntityToModel(entity = track))
    }

    override suspend fun insertSong(song: Song) {
        val songEntity = Converter.fromModelToEntity(song)
        favoriteDatabase.getSongDao().insertSongIntoTable(songEntity)
    }

    override suspend fun deleteSongByTrackId(trackId: Int) {
        favoriteDatabase.getSongDao().deleteSongByTrackId(trackId)
    }
}