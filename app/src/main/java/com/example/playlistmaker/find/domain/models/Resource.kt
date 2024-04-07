package com.example.playlistmaker.find.domain.models

sealed interface Resource<T> {
    data class Success<T>(val data: T): Resource<T>
    data class Error<T>(val errorCode: Int): Resource<T>
}