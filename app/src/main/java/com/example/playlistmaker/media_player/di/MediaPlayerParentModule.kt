package com.example.playlistmaker.media_player.di

import org.koin.dsl.module

val mediaPlayerParentModule = module {
    includes(mediaPlayerDataModule, mediaPlayerInteractorModule, mediaPlayerViewModelModule)
}