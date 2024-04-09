package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.domain.impl.ThemeSharedPreferenceImpl
import com.example.playlistmaker.settings.domain.repository.ThemeSharedPreference
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    factory <ThemeSharedPreference> {
        ThemeSharedPreferenceImpl(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
}