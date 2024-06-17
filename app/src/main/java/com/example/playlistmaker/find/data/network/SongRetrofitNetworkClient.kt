package com.example.playlistmaker.find.data.network

import com.example.playlistmaker.find.data.dto.SongDto
import com.example.playlistmaker.find.data.dto.SongResponse
import com.example.playlistmaker.find.data.repository.SongNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class SongRetrofitNetworkClient(
    private val service: SongApi
) : SongNetworkClient {
    override suspend fun doRequest(term: String): Response<SongResponse> {
        return withContext(Dispatchers.IO){
            service.search(term)
        }
    }
}