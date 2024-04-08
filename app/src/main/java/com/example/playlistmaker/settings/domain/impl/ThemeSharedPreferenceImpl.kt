package com.example.playlistmaker.settings.domain.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.repository.ThemeSharedPreference

class ThemeSharedPreferenceImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeSharedPreference {
    companion object {
        const val THEME_SWITCH_KEY = "THEME_SWITCH_KEY"
    }

    override fun getThemeFromSharedPreference(): Boolean {
        return sharedPreferences.getBoolean(
            THEME_SWITCH_KEY,
            AppCompatDelegate.MODE_NIGHT_NO != AppCompatDelegate.getDefaultNightMode()
        )
    }

    override fun setThemeToSharedPreference(theme: Boolean) = sharedPreferences.edit().putBoolean(
        THEME_SWITCH_KEY, theme
    ).apply()

}