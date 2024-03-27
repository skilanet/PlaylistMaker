package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Song

interface SharedPreferenceInteractor {
    fun getThiemeFromSharedPreference(): Boolean
    fun setThiemeToSharedPreference(thieme: Boolean)
    fun getSongsFromSharedPreference(): List<Song>
    fun setSongsToSharedPreference(songs: List<Song>)

    fun clearSharedPreference()
}