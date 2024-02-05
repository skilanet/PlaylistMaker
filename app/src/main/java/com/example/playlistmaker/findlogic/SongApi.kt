package com.example.playlistmaker.findlogic

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApi {

    @GET("/search?entity=song")
    fun search(
        @Query("term", encoded = false) title: String
    ): Call<SongResponse>
}