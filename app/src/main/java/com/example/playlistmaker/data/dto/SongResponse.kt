package com.example.playlistmaker.data.dto

class SongResponse(
    val resultCount: Int,
    val results: List<SongDto>
): Response()