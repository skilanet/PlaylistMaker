package com.example.playlistmaker.media_player.ui.models

sealed interface FavoriteState {
    data object InFavorite: FavoriteState
    data object NotInFavorite: FavoriteState
}