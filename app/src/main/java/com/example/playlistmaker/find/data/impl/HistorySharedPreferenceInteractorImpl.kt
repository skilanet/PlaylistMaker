package com.example.playlistmaker.find.data.impl

import android.content.Context
import com.example.playlistmaker.find.domain.models.HistoryOfSearch
import com.example.playlistmaker.find.domain.repository.HistorySharedPreferenceInteractor
import com.google.gson.Gson

class HistorySharedPreferenceInteractorImpl(context: Context) : HistorySharedPreferenceInteractor {
    private companion object {
        const val SHARED_PREFERENCE_NAME = "PLAYLISTMAKER_SHARED_PREFS"
        const val HISTORY_LIST_KEY = "ADD_HISTORY_LIST"
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    override fun getSongsFromSharedPreference(): HistoryOfSearch {
        val songString = sharedPreferences.getString(HISTORY_LIST_KEY, "")
        return fromJsonToHistory(songString.toString())
    }

    override fun setSongsToSharedPreference(historyOfSearch: HistoryOfSearch) {
        sharedPreferences.edit().putString(
            HISTORY_LIST_KEY, fromTracksToString(historyOfSearch)
        ).apply()
    }

    override fun clearSharedPreference() {
        sharedPreferences.edit().remove(HISTORY_LIST_KEY).apply()
    }

    private fun fromTracksToString(history: HistoryOfSearch): String = Gson().toJson(history)
    private fun fromJsonToHistory(json: String): HistoryOfSearch {
        return if (json.isNotEmpty()) Gson().fromJson(json, HistoryOfSearch::class.java)
        else HistoryOfSearch(emptyList())
    }
}
