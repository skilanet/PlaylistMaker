package com.example.playlistmaker.creator

import com.example.playlistmaker.data.network.SongRetrofitNetworkClient
import com.example.playlistmaker.data.repository.SharedPreferenceInteractorImpl
import com.example.playlistmaker.data.repository.SongRepositoryImpl
import com.example.playlistmaker.domain.impl.SongsUseCaseImpl
import com.example.playlistmaker.domain.repository.SharedPreferenceInteractor
import com.example.playlistmaker.domain.repository.SongRepository

object Creator {
    private fun provideSongsRepository(): SongRepository {
        return SongRepositoryImpl(SongRetrofitNetworkClient())
    }

    fun provideSongsUseCase(): SongsUseCaseImpl {
        return SongsUseCaseImpl(provideSongsRepository())
    }

    fun provideSharedPreferenceInteractor(): SharedPreferenceInteractor = SharedPreferenceInteractorImpl()
}