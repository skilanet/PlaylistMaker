package com.example.playlistmaker.presentation.find

import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackBaseBinding
import com.example.playlistmaker.domain.models.Song


class SongsViewHolder(
    private val binding: TrackBaseBinding,
) : RecyclerView.ViewHolder(
    binding.root
) {
    private val image: ImageView = binding.ivFindTrackImage
    private val trackName: TextView = binding.tvTrackName
    private val trackAuthor: TextView = binding.tvTrackAuthor
    private val trackTime: TextView = binding.tvTrackTime

    fun bind(model: Song) {
        val context = binding.root.context
        Glide.with(context).load(model.artworkUrl100).fitCenter()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(
                TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2f, context.resources.displayMetrics
            ).toInt())).into(image)
        trackName.text = model.trackName
        trackAuthor.text = model.artistName
        trackTime.text = model.currentTime
    }
}