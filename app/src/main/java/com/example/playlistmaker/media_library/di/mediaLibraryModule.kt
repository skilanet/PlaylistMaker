package com.example.playlistmaker.media_library.di

import androidx.room.Room
import com.example.playlistmaker.media_library.data.dao.AppDatabase
import com.example.playlistmaker.media_library.data.impl.FavoriteSongsRepositoryImpl
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsRepository
import com.example.playlistmaker.media_library.presentation.FavoriteTracksViewModel
import com.example.playlistmaker.media_library.presentation.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "favorite_db.db")
            .build()
    }

    single<FavoriteSongsRepository> { FavoriteSongsRepositoryImpl(get()) }

    viewModel {
        FavoriteTracksViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}