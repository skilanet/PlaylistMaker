package com.example.playlistmaker.di.media_player

import android.media.MediaPlayer
import com.example.playlistmaker.media_player.data.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor
import org.koin.dsl.module

val mediaPlayerInteractorModule = module {
    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get<String>(), get<MediaPlayer>())
    }
}