package com.example.playlistmaker.creator

import android.os.Handler
import com.example.playlistmaker.data.network.SongRetrofitNetworkClient
import com.example.playlistmaker.data.repository.DebounceInteractorImpl
import com.example.playlistmaker.data.repository.MediaPlayerInteractorImpl
import com.example.playlistmaker.data.repository.SharedPreferenceInteractorImpl
import com.example.playlistmaker.data.repository.SongRepositoryImpl
import com.example.playlistmaker.domain.impl.SongsUseCaseImpl
import com.example.playlistmaker.domain.repository.DebounceInteractor
import com.example.playlistmaker.domain.repository.MediaPlayerInteractor
import com.example.playlistmaker.domain.repository.SharedPreferenceInteractor
import com.example.playlistmaker.domain.repository.SongRepository

object Creator {
    private fun provideSongsRepository(setBackground:((Int) -> Unit)): SongRepository {
        return SongRepositoryImpl(SongRetrofitNetworkClient(), setBackground)
    }

    fun provideSongsUseCase(setBackground:((Int) -> Unit)): SongsUseCaseImpl {
        return SongsUseCaseImpl(provideSongsRepository(setBackground))
    }

    fun provideSharedPreferenceInteractor(): SharedPreferenceInteractor = SharedPreferenceInteractorImpl()

    fun provideDebounceRepository(handler: Handler): DebounceInteractor =
        DebounceInteractorImpl(handler)

    fun provideMediaPlayerInteractor(url: String, handler: Handler): MediaPlayerInteractor =
        MediaPlayerInteractorImpl(url, handler)
}