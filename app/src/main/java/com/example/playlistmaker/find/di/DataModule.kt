package com.example.playlistmaker.find.di

import android.content.Context
import com.example.playlistmaker.find.data.impl.HistorySharedPreferenceImpl
import com.example.playlistmaker.find.data.network.SongApi
import com.example.playlistmaker.find.data.network.SongRetrofitNetworkClient
import com.example.playlistmaker.find.data.repository.SongNetworkClient
import com.example.playlistmaker.find.domain.repository.HistorySharedPreference
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val findDataModule = module {
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
}