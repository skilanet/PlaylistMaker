package com.example.playlistmaker.find.domain.repository

import com.example.playlistmaker.find.domain.models.HistoryOfSearch

interface HistorySharedPreference {
    fun getSongsFromSharedPreference(): HistoryOfSearch
    fun setSongsToSharedPreference(historyOfSearch: HistoryOfSearch)
    fun clearSharedPreference()
}