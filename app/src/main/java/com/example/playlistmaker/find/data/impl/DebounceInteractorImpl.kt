package com.example.playlistmaker.find.data.impl

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.find.domain.repository.DebounceInteractor

class DebounceInteractorImpl : DebounceInteractor {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    override fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}