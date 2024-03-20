package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.SongResponse
import com.example.playlistmaker.data.mapper.SongMapper
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Song
import com.example.playlistmaker.domain.repository.SongRepository

class SongRepositoryImpl(private val networkClient: SongNetworkClient) : SongRepository {
    override fun getSongs(term: String): List<Song> {
        val response = networkClient.doRequest(term)
        return if (response.resultCode == 200){
            val songs = (response as SongResponse).results.map {songDto -> SongMapper.map(songDto)}
            songs
        }else emptyList()
    }
}