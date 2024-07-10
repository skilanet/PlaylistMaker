package com.example.playlistmaker.new_playlist.domain.repository

import kotlinx.coroutines.flow.Flow

interface FileRepository {
    suspend fun saveToLocalStorage(uri: String, playlistName: String)
    fun getFromLocalStorage(playlistName: String): Flow<String>
}