package com.example.playlistmaker.di.media_player

import org.koin.dsl.module

val mediaPlayerParentModule = module {
    includes(mediaPlayerDataModule, mediaPlayerInteractorModule, mediaPlayerViewModelModule)
}