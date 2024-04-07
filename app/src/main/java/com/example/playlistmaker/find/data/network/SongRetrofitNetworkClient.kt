package com.example.playlistmaker.find.data.network

import com.example.playlistmaker.find.data.dto.Response
import com.example.playlistmaker.find.data.repository.SongNetworkClient

class SongRetrofitNetworkClient: SongNetworkClient {
    override fun doRequest(term: String): Response {
        return try {
            val response = RetrofitClient.api.search(term).execute()
            val networkResponse = response.body() ?: Response()

            networkResponse.apply { resultCode = response.code() }
        } catch (ex: Exception){
            Response().apply { resultCode = 400 }
        }
    }
}