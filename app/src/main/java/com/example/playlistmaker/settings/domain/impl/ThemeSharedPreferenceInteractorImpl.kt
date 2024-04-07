package com.example.playlistmaker.settings.domain.impl

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.repository.ThemeSharedPreferenceInteractor

class ThemeSharedPreferenceInteractorImpl(context: Context) : ThemeSharedPreferenceInteractor {
    companion object {
        const val SHARED_PREFERENCE_NAME = "PLAYLISTMAKER_SHARED_PREFS"
        const val THEME_SWITCH_KEY = "THEME_SWITCH_KEY"
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
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