package com.example.playlistmaker.find.data.impl

import com.example.playlistmaker.find.data.dto.SongResponse
import com.example.playlistmaker.find.data.mapper.SongMapper
import com.example.playlistmaker.find.data.repository.SongNetworkClient
import com.example.playlistmaker.find.domain.models.Resource
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.find.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SongRepositoryImpl(private val networkClient: SongNetworkClient) : SongRepository {
    override suspend fun getSongs(term: String): Flow<Resource<List<Song>>> = flow {
        val response = networkClient.doRequest(term)
        if (response.isSuccessful){
            val data = SongMapper.map(response.body()?.results)
            emit(Resource.Success(data, response.code()))
        }
        else emit(Resource.Error(response.code()))
    }
}