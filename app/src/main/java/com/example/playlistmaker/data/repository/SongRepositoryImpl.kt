package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.SongResponse
import com.example.playlistmaker.data.mapper.SongMapper
import com.example.playlistmaker.domain.models.Song
import com.example.playlistmaker.domain.repository.SongRepository

class SongRepositoryImpl(private val networkClient: SongNetworkClient,
    private val setBackground: (Int) -> Unit) : SongRepository {

    companion object {
        const val SHOW_R = 0x000000
        const val SHOW_NNF = 0x000001
        const val SHOW_NIC = 0x000002
        const val HIDE_ALL = 0x000003
    }
    override fun getSongs(term: String): List<Song> {
        setBackground.invoke(HIDE_ALL)
        val response = networkClient.doRequest(term)
        return if (response is SongResponse) {
            if (response.resultCode == 200) {
                if (response.results.isNotEmpty()) {
                    setBackground.invoke(SHOW_R)
                    val songs = response.results.map { songDto -> SongMapper.map(songDto) }
                    songs
                } else {
                    setBackground.invoke(SHOW_NNF)
                    emptyList()
                }
            } else {
                setBackground.invoke(SHOW_NIC)
                emptyList()
            }

        }else emptyList()
    }
}