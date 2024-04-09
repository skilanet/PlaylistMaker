package com.example.playlistmaker.media_player.di

import android.media.MediaPlayer
import org.koin.dsl.module

val mediaPlayerDataModule = module {
    factory<MediaPlayer> { MediaPlayer() }
}