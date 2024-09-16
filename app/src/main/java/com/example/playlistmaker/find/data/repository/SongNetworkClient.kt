package com.example.playlistmaker.find.data.repository

import com.example.playlistmaker.find.data.dto.Response

interface SongNetworkClient {
    suspend fun doRequest(term: String): Response
}