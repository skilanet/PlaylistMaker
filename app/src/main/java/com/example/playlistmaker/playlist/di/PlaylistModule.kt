package com.example.playlistmaker.playlist.di

import com.example.playlistmaker.playlist.data.PlaylistRepositoryImpl
import com.example.playlistmaker.playlist.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.playlist.domain.repository.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.repository.PlaylistRepository
import com.example.playlistmaker.playlist.presentation.PlaylistViewmodel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get())
    }
    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
    viewModel { (id: Int) ->
        PlaylistViewmodel(id, get())
    }
}