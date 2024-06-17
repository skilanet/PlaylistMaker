package com.example.playlistmaker.find.data.network

import com.example.playlistmaker.find.data.dto.SongResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApi {

    @GET("/search?entity=song")
    fun search(
        @Query("term", encoded = false) term: String
    ): Response<SongResponse>
}