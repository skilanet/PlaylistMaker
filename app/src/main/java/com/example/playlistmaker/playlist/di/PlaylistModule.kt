package com.example.playlistmaker.playlist.di

import com.example.playlistmaker.playlist.presentation.PlaylistViewmodel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {
    viewModel {
        PlaylistViewmodel()
    }
}