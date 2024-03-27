package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

class App : Application() {

    private var darkThieme = false
    private val sharedPreferenceInteractor = Creator.provideSharedPreferenceInteractor()
    override fun onCreate() {
        super.onCreate()
        darkThieme = sharedPreferenceInteractor.getThiemeFromSharedPreference()
        sharedPreferenceInteractor.setThiemeToSharedPreference(darkThieme)
        switchThieme(darkThieme)
    }

    fun switchThieme(darkThiemeEnable: Boolean) {
        darkThieme = darkThiemeEnable
        AppCompatDelegate.setDefaultNightMode(
            if (darkThiemeEnable) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}