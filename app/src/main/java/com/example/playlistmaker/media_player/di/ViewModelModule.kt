package com.example.playlistmaker.media_player.di

import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor
import com.example.playlistmaker.media_player.presentation.view_model.MediaPlayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val mediaPlayerViewModelModule = module {
    viewModel { (url: String) ->
        MediaPlayerViewModel(
            mediaPlayerInteractor = get<MediaPlayerInteractor>(parameters = { parametersOf(url) }),
            applicationContext = androidContext()
        )
    }
}