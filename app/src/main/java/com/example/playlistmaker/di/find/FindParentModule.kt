package com.example.playlistmaker.di.find

import org.koin.dsl.module

val findParentModule = module {
    includes(findInteractorModule, findRepositoryModule, findDataModule, findViewModelModule)
}