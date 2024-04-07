package com.example.playlistmaker.find.domain.repository

import com.example.playlistmaker.find.domain.models.Resource
import com.example.playlistmaker.find.domain.models.Song

interface SongRepository {
    fun getSongs(term: String): Resource<List<Song>>
}