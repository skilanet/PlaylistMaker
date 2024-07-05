package com.example.playlistmaker.new_playlist.di

import com.example.playlistmaker.new_playlist.data.impl.PlaylistRepositoryImpl
import com.example.playlistmaker.new_playlist.domain.impl.PlaylistsInteractorIml
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistRepository
import com.example.playlistmaker.new_playlist.domain.repository.PlaylistsInteractor
import com.example.playlistmaker.new_playlist.presentation.AddPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newPlaylistModule = module {
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get())
    }
    factory<PlaylistsInteractor> {
        PlaylistsInteractorIml(get())
    }

    viewModel {
        AddPlaylistViewModel(get())
    }
}