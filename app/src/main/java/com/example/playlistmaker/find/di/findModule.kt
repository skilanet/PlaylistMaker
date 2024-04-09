package com.example.playlistmaker.find.di

import android.content.Context
import com.example.playlistmaker.find.data.impl.DebounceInteractorImpl
import com.example.playlistmaker.find.data.impl.HistorySharedPreferenceImpl
import com.example.playlistmaker.find.data.impl.SongRepositoryImpl
import com.example.playlistmaker.find.data.network.SongApi
import com.example.playlistmaker.find.data.network.SongRetrofitNetworkClient
import com.example.playlistmaker.find.data.repository.SongNetworkClient
import com.example.playlistmaker.find.domain.impl.SongsInteractorImpl
import com.example.playlistmaker.find.domain.repository.DebounceInteractor
import com.example.playlistmaker.find.domain.repository.HistorySharedPreference
import com.example.playlistmaker.find.domain.repository.SongRepository
import com.example.playlistmaker.find.domain.repository.SongsInteractor
import com.example.playlistmaker.find.presentation.view_model.FindViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val findModule = module {
    single {
        androidContext().getSharedPreferences("PLAYLISTMAKER_SHARED_PREFS", Context.MODE_PRIVATE)
    }
    factory { Gson() }
    single<SongApi> {
        Retrofit.Builder().baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create()).build().create(SongApi::class.java)
    }
    single<SongNetworkClient> {
        SongRetrofitNetworkClient(get())
    }
    single<HistorySharedPreference> {
        HistorySharedPreferenceImpl(get(), get())
    }

    single<DebounceInteractor> {
        DebounceInteractorImpl()
    }
    single<SongsInteractor> {
        SongsInteractorImpl(get())
    }

    single<SongRepository> {
        SongRepositoryImpl(get())
    }

    viewModel {
        FindViewModel(get(), get())
    }
}