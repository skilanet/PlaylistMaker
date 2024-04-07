package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.utils.Creator

class App : Application() {

    private var darkTheme = false
    private val sharedPreferenceInteractor = Creator.provideThemeSharedPreference(this)
    override fun onCreate() {
        super.onCreate()
        darkTheme = sharedPreferenceInteractor.getThemeFromSharedPreference()
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnable: Boolean) {
        darkTheme = darkThemeEnable
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnable) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}