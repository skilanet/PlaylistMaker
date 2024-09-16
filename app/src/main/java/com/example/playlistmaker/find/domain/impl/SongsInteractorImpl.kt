package com.example.playlistmaker.find.domain.impl

import com.example.playlistmaker.find.domain.models.Resource
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.find.domain.repository.SongsInteractor
import com.example.playlistmaker.find.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class SongsInteractorImpl(private val repository: SongRepository): SongsInteractor {
    override suspend fun searchSongs(term: String): Flow<Pair<List<Song>?, Int>> {
        return repository.getSongs(term).mapNotNull { result ->
            when(result){
                is Resource.Success-> Pair(result.data, result.code)
                is Resource.Error -> Pair(null, result.code)
            }
        }
    }

}