package com.example.playlistmaker.settings.domain.repository

interface ThemeSharedPreference {
    fun getThemeFromSharedPreference(): Boolean
    fun setThemeToSharedPreference(theme: Boolean)
}