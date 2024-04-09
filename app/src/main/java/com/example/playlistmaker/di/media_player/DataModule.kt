package com.example.playlistmaker.di.media_player

import android.media.MediaPlayer
import org.koin.dsl.module

val mediaPlayerDataModule = module {
    factory<MediaPlayer> { MediaPlayer() }
}