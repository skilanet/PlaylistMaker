package com.example.playlistmaker.find.data.impl

import com.example.playlistmaker.find.data.dto.SongResponse
import com.example.playlistmaker.find.data.mapper.SongMapper
import com.example.playlistmaker.find.data.repository.SongNetworkClient
import com.example.playlistmaker.find.domain.models.Resource
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.find.domain.repository.SongRepository

class SongRepositoryImpl(private val networkClient: SongNetworkClient) : SongRepository {

    override fun getSongs(term: String): Resource<List<Song>> {
        val response = networkClient.doRequest(term)
        if (response is SongResponse) {
            if (response.results.isNotEmpty()) {
                val songs = response.results.map { SongMapper.map(it) }
                return Resource.Success(songs)
            } else return Resource.Success(emptyList())
        } else {
            return Resource.Error(response.resultCode)
        }
    }
}