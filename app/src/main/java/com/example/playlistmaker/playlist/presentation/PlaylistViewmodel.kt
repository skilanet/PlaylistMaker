package com.example.playlistmaker.playlist.presentation

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.find.domain.models.Song
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewmodel : ViewModel() {

    fun calculateTime(array: List<Song>): Int {
        val time = array.sumOf { it.trackTimeMillis }
        return SimpleDateFormat("mm", Locale.getDefault()).format(time).toInt()
    }
}