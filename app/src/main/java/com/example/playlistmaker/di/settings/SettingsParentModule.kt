package com.example.playlistmaker.di.settings

import org.koin.dsl.module

val settingsParentModule = module {
    includes(settingsDataModule, settingsViewModelModule)
}