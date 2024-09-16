package com.example.playlistmaker.find.data.network

import com.example.playlistmaker.find.data.dto.SongResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApi {

    @GET("/search?entity=song")
    suspend fun search(
        @Query("term", encoded = false) term: String
    ): SongResponse
}