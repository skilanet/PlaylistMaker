package com.example.playlistmaker.findactivity

import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.Utils
import com.example.playlistmaker.databinding.TrackBaseBinding
import java.text.SimpleDateFormat
import java.util.Locale


class TrackViewHolder(binding: TrackBaseBinding) : RecyclerView.ViewHolder(binding.root) {
    private val image: ImageView = binding.ivFindTrackImage
    private val trackName: TextView = binding.tvTrackName
    private val trackAuthor: TextView = binding.tvTrackAuthor
    private val trackTime: TextView = binding.tvTrackTime
    private val arrowRight: ImageView = binding.ivFindArrowRight

    fun bind(model: SongDescription) {
        Glide.with(itemView).load(model.artworkUrl100).fitCenter()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(Utils.pxtodp(2f, itemView.context))).into(image)
        trackName.text = model.trackName
        trackAuthor.text = model.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L)
        arrowRight.setOnClickListener {
            Toast.makeText(
                itemView.context,
                itemView.context.getString(R.string.button_plug), Toast.LENGTH_LONG
            ).show()
        }
    }
}