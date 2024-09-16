package com.example.playlistmaker.media_player.di

import android.media.MediaPlayer
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_player.presentation.view_model.MediaPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaPlayerModule = module {
    factory<MediaPlayer> { MediaPlayer() }

    factory {
        MediaPlayer()
    }

    viewModel { (song: Song) ->
        MediaPlayerViewModel(
            song = song,
            mediaPlayer = get(),
            favoriteSongsInteractor = get(),
            playlistsInteractor = get()
        )
    }
}