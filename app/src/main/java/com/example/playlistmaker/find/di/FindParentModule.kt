package com.example.playlistmaker.find.di

import org.koin.dsl.module

val findParentModule = module {
    includes(findInteractorModule, findRepositoryModule, findDataModule, findViewModelModule)
}