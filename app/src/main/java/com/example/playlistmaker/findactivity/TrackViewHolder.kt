package com.example.playlistmaker.findactivity

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R


class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val image: ImageView
    private val trackName: TextView
    private val trackAuthor: TextView
    private val trackTime: TextView
    private val arrowRight: ImageView

    init {
        image = view.findViewById(R.id.iv_find_track_image)
        trackName = view.findViewById(R.id.tv_track_name)
        trackAuthor = view.findViewById(R.id.tv_track_author)
        trackTime = view.findViewById(R.id.tv_track_time)
        arrowRight = view.findViewById(R.id.iv_find_arrow_right)
    }

    fun bind(model: Track) {
        Log.d(RECYCLER_TEST_TAG, "Bind started")
        Glide.with(itemView).load(model.artworkUrl100).fitCenter()
            .placeholder(R.drawable.find_image_plug)
            .transform(RoundedCorners(pxtodp(2f, itemView.context))).into(image)
        trackName.text = model.trackName
        trackAuthor.text = model.artistName
        trackTime.text = model.trackTime
    }

    private fun pxtodp(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }
}