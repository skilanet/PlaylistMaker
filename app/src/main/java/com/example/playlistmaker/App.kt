package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.objects.consts.SharedPreference

class App : Application() {

    private var darkThieme = false

    private val sharedPrefs by lazy {
        getSharedPreferences(SharedPreference.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
    }
    override fun onCreate() {
        super.onCreate()
        darkThieme = sharedPrefs.getBoolean(SharedPreference.THIEME_SWITCH_KEY, false)
        sharedPrefs.edit()
            .putBoolean(SharedPreference.THIEME_SWITCH_KEY, darkThieme)
            .apply()
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