package com.example.playlistmaker.find.data.repository

import com.example.playlistmaker.find.data.dto.SongResponse
import retrofit2.Response

interface SongNetworkClient {
    suspend fun doRequest(term: String): Response<SongResponse>
}