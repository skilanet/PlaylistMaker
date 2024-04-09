package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.domain.impl.ThemeSharedPreferenceImpl
import com.example.playlistmaker.settings.domain.repository.ThemeSharedPreference
import org.koin.dsl.module

val settingsDataModule = module {
    factory <ThemeSharedPreference> {
        ThemeSharedPreferenceImpl(get())
    }
}