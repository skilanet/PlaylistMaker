package com.example.playlistmaker.objects

import android.content.Context
import android.util.TypedValue
import com.example.playlistmaker.findlogic.SongDescription
import com.google.gson.Gson

object Utils {
    fun pxtodp(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }

    fun createJsonFromSong(songDescription: SongDescription): String = Gson().toJson(songDescription)
    fun createSongFromJson(json: String): SongDescription = Gson().fromJson(json, SongDescription::class.java)
}