package com.example.playlistmaker.objects

import android.content.Context
import android.util.TypedValue

object Utils {
    fun pxtodp(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }
}