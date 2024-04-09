package com.example.playlistmaker.settings.di

import org.koin.dsl.module

val settingsParentModule = module {
    includes(settingsDataModule, settingsRepositoryModule, settingsViewModelModule)
}