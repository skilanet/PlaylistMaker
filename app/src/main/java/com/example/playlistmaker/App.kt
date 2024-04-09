package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.find.di.findParentModule
import com.example.playlistmaker.media_player.di.mediaPlayerParentModule
import com.example.playlistmaker.settings.di.settingsParentModule
import com.example.playlistmaker.settings.domain.repository.ThemeSharedPreference
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(findParentModule, mediaPlayerParentModule, settingsParentModule)
        }
        val sharedPreference = getKoin().get<ThemeSharedPreference>()
        darkTheme = sharedPreference.getThemeFromSharedPreference()
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