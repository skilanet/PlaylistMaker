package com.example.playlistmaker.di.domain

import com.example.playlistmaker.find.data.impl.SongRepositoryImpl
import com.example.playlistmaker.find.domain.repository.SongRepository
import com.example.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.repository.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    //find
    single<SongRepository> {
        SongRepositoryImpl(get())
    }

    //media_player
    //settings
    //sharing
    single<SharingRepository>{
        SharingRepositoryImpl(androidContext())
    }
}