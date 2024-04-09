package com.example.playlistmaker.media_player.di

import android.media.MediaPlayer
import com.example.playlistmaker.media_player.data.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor
import com.example.playlistmaker.media_player.presentation.view_model.MediaPlayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val mediaPlayerModule = module {
    factory<MediaPlayer> { MediaPlayer() }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get<String>(), get<MediaPlayer>())
    }

    viewModel { (url: String) ->
        MediaPlayerViewModel(
            mediaPlayerInteractor = get<MediaPlayerInteractor>(parameters = { parametersOf(url) }),
            applicationContext = androidContext()
        )
    }
}