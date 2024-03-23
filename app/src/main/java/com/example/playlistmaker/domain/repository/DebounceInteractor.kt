package com.example.playlistmaker.domain.repository

interface DebounceInteractor {
    fun clickDebounce(): Boolean
    fun searchDebounce(searchRunnable: Runnable)
}