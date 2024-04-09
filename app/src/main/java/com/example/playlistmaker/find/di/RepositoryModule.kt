package com.example.playlistmaker.find.di

import com.example.playlistmaker.find.data.impl.SongRepositoryImpl
import com.example.playlistmaker.find.domain.repository.SongRepository
import org.koin.dsl.module

val findRepositoryModule = module {
    single<SongRepository> {
        SongRepositoryImpl(get())
    }
}