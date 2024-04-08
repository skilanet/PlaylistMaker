package com.example.playlistmaker.di

import com.example.playlistmaker.find.presentation.view_model.FindViewModel
import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor
import com.example.playlistmaker.media_player.presentation.view_model.MediaPlayerViewModel
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {

    //find
    viewModel {
        FindViewModel(get(), get())
    }
    viewModel { (url: String) ->
        MediaPlayerViewModel(
            mediaPlayerInteractor = get<MediaPlayerInteractor>(parameters = { parametersOf(url) }),
            applicationContext = androidContext()
        )
    }
    viewModel {
        SettingsViewModel(get(), get())
    }
}