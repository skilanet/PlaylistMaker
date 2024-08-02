package com.example.playlistmaker.new_playlist.di

import androidx.room.Room
import com.example.playlistmaker.new_playlist.data.dao.PlaylistsDatabase
import com.example.playlistmaker.new_playlist.data.impl.FilesRepositoryImpl
import com.example.playlistmaker.new_playlist.data.impl.PlaylistsRepositoryImpl
import com.example.playlistmaker.new_playlist.domain.impl.FileInteractorImpl
import com.example.playlistmaker.new_playlist.domain.impl.PlaylistsInteractorIml
import com.example.playlistmaker.new_playlist.domain.repository.FileInteractor
import com.example.playlistmaker.new_playlist.domain.repository.FileRepository
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsRepository
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsInteractor
import com.example.playlistmaker.new_playlist.presentation.AddPlaylistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newPlaylistModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            PlaylistsDatabase::class.java,
            "playlists_database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get<PlaylistsDatabase>())
    }

    single<PlaylistsInteractor> {
        PlaylistsInteractorIml(get())
    }

    single<FileRepository> {
        FilesRepositoryImpl(androidContext())
    }

    single<FileInteractor> {
        FileInteractorImpl(get())
    }

    viewModel {
        AddPlaylistViewModel(get(), get())
    }
}