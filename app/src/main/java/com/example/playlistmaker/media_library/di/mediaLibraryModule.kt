package com.example.playlistmaker.media_library.di

import androidx.room.Room
import com.example.playlistmaker.media_library.data.dao.FavoriteDatabase
import com.example.playlistmaker.media_library.data.impl.FavoriteSongsRepositoryImpl
import com.example.playlistmaker.media_library.domain.impl.FavoriteSongsInteractorImpl
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsInteractor
import com.example.playlistmaker.media_library.domain.repository.FavoriteSongsRepository
import com.example.playlistmaker.media_library.presentation.FavoriteTracksViewModel
import com.example.playlistmaker.media_library.presentation.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            FavoriteDatabase::class.java,
            "favorite_database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single<FavoriteSongsRepository> { FavoriteSongsRepositoryImpl(get()) }

    single<FavoriteSongsInteractor> { FavoriteSongsInteractorImpl(get()) }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }
}