package com.example.playlistmaker.find.data.repository

import com.example.playlistmaker.find.data.dto.Response

interface SongNetworkClient {
    fun doRequest(term: String): Response
}