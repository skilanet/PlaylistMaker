package com.example.playlistmaker.utils

import android.content.Context
import android.os.Handler
import com.example.playlistmaker.find.data.impl.DebounceInteractorImpl
import com.example.playlistmaker.find.data.impl.HistorySharedPreferenceInteractorImpl
import com.example.playlistmaker.find.data.impl.SongRepositoryImpl
import com.example.playlistmaker.find.data.network.SongRetrofitNetworkClient
import com.example.playlistmaker.find.domain.impl.SongsUseCaseImpl
import com.example.playlistmaker.find.domain.repository.DebounceInteractor
import com.example.playlistmaker.find.domain.repository.HistorySharedPreferenceInteractor
import com.example.playlistmaker.find.domain.repository.SongRepository
import com.example.playlistmaker.media_player.data.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.media_player.domain.repository.MediaPlayerInteractor
import com.example.playlistmaker.settings.domain.impl.ThemeSharedPreferenceInteractorImpl
import com.example.playlistmaker.settings.domain.repository.ThemeSharedPreferenceInteractor
import com.example.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.repository.SharingRepository

object Creator {
    private fun provideSongsRepository(): SongRepository {
        return SongRepositoryImpl(SongRetrofitNetworkClient())
    }

    fun provideSongsUseCase(): SongsUseCaseImpl {
        return SongsUseCaseImpl(provideSongsRepository())
    }

    fun provideHistorySharedPreferenceInteractor(context: Context): HistorySharedPreferenceInteractor =
        HistorySharedPreferenceInteractorImpl(context)

    fun provideThemeSharedPreference(context: Context): ThemeSharedPreferenceInteractor =
        ThemeSharedPreferenceInteractorImpl(context)

    fun provideDebounceRepository(handler: Handler): DebounceInteractor =
        DebounceInteractorImpl(handler)

    fun provideMediaPlayerInteractor(url: String): MediaPlayerInteractor =
        MediaPlayerInteractorImpl(url)

    fun provideSharingRepository(context: Context): SharingRepository =
        SharingRepositoryImpl(context)
}