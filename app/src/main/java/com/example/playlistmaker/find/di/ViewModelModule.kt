package com.example.playlistmaker.find.di

import com.example.playlistmaker.find.presentation.view_model.FindViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val findViewModelModule = module {
    viewModel {
        FindViewModel(get(), get())
    }
}