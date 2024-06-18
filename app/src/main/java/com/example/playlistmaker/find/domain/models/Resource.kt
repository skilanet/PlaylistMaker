package com.example.playlistmaker.find.domain.models

sealed class Resource<T>(val data: T? = null, val code: Int) {
    class Success<T>(data: T, code: Int): Resource<T>(data, code)
    class Error<T>(code: Int): Resource<T>(code = code)
}