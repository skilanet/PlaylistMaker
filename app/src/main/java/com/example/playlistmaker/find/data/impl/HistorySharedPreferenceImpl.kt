package com.example.playlistmaker.find.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.find.domain.models.HistoryOfSearch
import com.example.playlistmaker.find.domain.repository.HistorySharedPreference
import com.google.gson.Gson

class HistorySharedPreferenceImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : HistorySharedPreference {
    private companion object {
        const val HISTORY_LIST_KEY = "ADD_HISTORY_LIST"
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

    private fun fromTracksToString(history: HistoryOfSearch): String = gson.toJson(history)
    private fun fromJsonToHistory(json: String): HistoryOfSearch {
        return if (json.isNotEmpty()) gson.fromJson(json, HistoryOfSearch::class.java)
        else HistoryOfSearch(ArrayList())
    }
}
