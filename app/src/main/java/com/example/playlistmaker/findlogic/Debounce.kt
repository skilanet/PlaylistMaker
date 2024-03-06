package com.example.playlistmaker.findlogic

import android.os.Looper
import android.os.Handler


class Debounce{
    companion object{
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    fun clickDebounce(): Boolean{
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}