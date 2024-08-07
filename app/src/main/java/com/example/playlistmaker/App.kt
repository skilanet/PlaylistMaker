package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.find.di.findModule
import com.example.playlistmaker.media_library.di.mediaLibraryModule
import com.example.playlistmaker.media_player.di.mediaPlayerModule
import com.example.playlistmaker.new_playlist.di.newPlaylistModule
import com.example.playlistmaker.playlist.di.playlistModule
import com.example.playlistmaker.settings.di.settingsModule
import com.example.playlistmaker.settings.domain.repository.ThemeSharedPreference
import com.example.playlistmaker.sharing.di.sharingModule
import com.markodevcic.peko.PermissionRequester
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        PermissionRequester.initialize(this)
        startKoin {
            androidContext(this@App)
            modules(
                findModule,
                mediaPlayerModule,
                settingsModule,
                sharingModule,
                mediaLibraryModule,
                newPlaylistModule,
                playlistModule
            )
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