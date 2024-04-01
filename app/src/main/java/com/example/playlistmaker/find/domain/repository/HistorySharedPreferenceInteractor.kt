package com.example.playlistmaker.find.domain.repository

import com.example.playlistmaker.find.domain.models.HistoryOfSearch

interface HistorySharedPreferenceInteractor {
    fun getSongsFromSharedPreference(): HistoryOfSearch
    fun setSongsToSharedPreference(historyOfSearch: HistoryOfSearch)

    fun clearSharedPreference()
}