package com.example.playlistmaker.find.data.network

import com.example.playlistmaker.find.data.dto.Response
import com.example.playlistmaker.find.data.repository.SongNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongRetrofitNetworkClient(
    private val service: SongApi
) : SongNetworkClient {
    override suspend fun doRequest(term: String): Response {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.search(term)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 400 }
            }
        }
    }
}