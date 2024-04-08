package com.example.playlistmaker.di.domain

import com.example.playlistmaker.find.data.impl.DebounceInteractorImpl
import com.example.playlistmaker.find.domain.impl.SongsInteractorImpl
import com.example.playlistmaker.find.domain.repository.DebounceInteractor
import com.example.playlistmaker.find.domain.repository.SongsInteractor
import com.example.playlistmaker.media_player.data.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor
import org.koin.dsl.module

val interactorModule = module {

    //find
    single<DebounceInteractor> {
        DebounceInteractorImpl()
    }

    single<SongsInteractor> {
        SongsInteractorImpl(get())
    }

    //media_player
    factory<MediaPlayerInteractor>(
    ) {
        MediaPlayerInteractorImpl(get())
    }
    //settings
    //sharing
}