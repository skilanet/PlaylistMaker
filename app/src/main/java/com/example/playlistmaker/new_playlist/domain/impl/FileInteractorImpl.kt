package com.example.playlistmaker.new_playlist.domain.impl

import com.example.playlistmaker.new_playlist.domain.repository.FileInteractor
import com.example.playlistmaker.new_playlist.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow

class FileInteractorImpl(private val fileRepository: FileRepository) :
    FileInteractor {
    override suspend fun saveToLocalStorage(uri: String, playlistName: String) {
        fileRepository.saveToLocalStorage(uri, playlistName)
    }

    override fun getFromLocalStorage(playlistName: String): Flow<String> =
        fileRepository.getFromLocalStorage(playlistName)
}