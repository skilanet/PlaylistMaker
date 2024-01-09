package com.example.playlistmaker.findactivity

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.Utils


class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val image: ImageView = view.findViewById(R.id.iv_find_track_image)
    private val trackName: TextView = view.findViewById(R.id.tv_track_name)
    private val trackAuthor: TextView = view.findViewById(R.id.tv_track_author)
    private val trackTime: TextView = view.findViewById(R.id.tv_track_time)
    private val arrowRight: ImageView = view.findViewById(R.id.iv_find_arrow_right)

    fun bind(model: Track) {
        Glide.with(itemView).load(model.artworkUrl100).fitCenter()
            .placeholder(R.drawable.find_image_plug)
            .transform(RoundedCorners(Utils.pxtodp(2f, itemView.context))).into(image)
        trackName.text = model.trackName
        trackAuthor.text = model.artistName
        trackTime.text = model.trackTime
    }
}