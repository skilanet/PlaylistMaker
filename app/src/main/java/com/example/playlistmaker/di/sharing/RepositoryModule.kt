package com.example.playlistmaker.di.sharing

import com.example.playlistmaker.sharing.data.impl.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.repository.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingRepositoryModule = module {
    single<SharingRepository>{
        SharingRepositoryImpl(androidContext())
    }
}