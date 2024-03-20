package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Song

interface SongRepository {
    fun getSongs(term: String): List<Song>
}