package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.Response

interface SongNetworkClient {
    fun doRequest(term: String): Response
}