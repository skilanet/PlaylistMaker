package com.example.playlistmaker.data.repository

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.models.HistoryOfSearch
import com.example.playlistmaker.domain.models.Song
import com.example.playlistmaker.domain.repository.SharedPreferenceInteractor
import com.google.gson.Gson

class SharedPreferenceInteractorImpl : SharedPreferenceInteractor {
    private companion object {
        const val SHARED_PREFERENCE_NAME = "PLAYLISTMAKER_SHARED_PREFS"
        const val THIEME_SWITCH_KEY = "THIEME_SWITCH_KEY"
        const val HISTORY_LIST_KEY = "ADD_HISTORY_LIST"
    }

    private val sharedPreferences by lazy {
        Activity().getSharedPreferences(SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE)
    }

    override fun getThiemeFromSharedPreference(): Boolean {
        return sharedPreferences.getBoolean(
            THIEME_SWITCH_KEY,
            AppCompatDelegate.MODE_NIGHT_NO != AppCompatDelegate.getDefaultNightMode()
        )
    }

    override fun setThiemeToSharedPreference(thieme: Boolean) = sharedPreferences.edit().putBoolean(
        THIEME_SWITCH_KEY, thieme
    ).apply()

    override fun getSongsFromSharedPreference(): List<Song> {
        val songString = sharedPreferences.getString(HISTORY_LIST_KEY, null)
        return if (songString != null) Gson().fromJson(
            songString, HistoryOfSearch::class.java
        ).history
        else emptyList()
    }

    override fun setSongsToSharedPreference(songs: List<Song>) =
        sharedPreferences.edit().putString(HISTORY_LIST_KEY, Gson().toJson(songs)).apply()

    override fun clearSharedPreference() {
        sharedPreferences.edit().remove(HISTORY_LIST_KEY).apply()
    }

}