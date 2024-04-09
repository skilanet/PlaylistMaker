package com.example.playlistmaker.find.di

import com.example.playlistmaker.find.data.impl.DebounceInteractorImpl
import com.example.playlistmaker.find.domain.impl.SongsInteractorImpl
import com.example.playlistmaker.find.domain.repository.DebounceInteractor
import com.example.playlistmaker.find.domain.repository.SongsInteractor
import org.koin.dsl.module

val findInteractorModule = module {
    single<DebounceInteractor> {
        DebounceInteractorImpl()
    }
    single<SongsInteractor> {
        SongsInteractorImpl(get())
    }
}