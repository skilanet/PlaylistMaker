package com.example.playlistmaker.settings.domain.repository

interface ThemeSharedPreferenceInteractor {
    fun getThemeFromSharedPreference(): Boolean
    fun setThemeToSharedPreference(theme: Boolean)
}