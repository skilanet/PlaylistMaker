package com.example.playlistmaker.media_library.ui

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackBaseBinding
import com.example.playlistmaker.find.domain.models.Song

class FavoriteTracksAdapter :
    RecyclerView.Adapter<FavoriteTracksAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: TrackBaseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val image: ImageView = binding.ivFindTrackImage
        private val trackName: TextView = binding.tvTrackName
        private val trackAuthor: TextView = binding.tvTrackAuthor
        private val trackTime: TextView = binding.tvTrackTime
        fun bind(model: Song) {
            val context = binding.root.context
            Glide.with(context).load(model.artworkUrl100).fitCenter()
                .placeholder(R.drawable.placeholder)
                .transform(
                    RoundedCorners(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 2f, context.resources.displayMetrics
                        ).toInt()
                    )
                ).into(image)
            trackName.text = model.trackName
            trackAuthor.text = model.artistName
            trackTime.text = model.currentTime
        }
    }

    var tracks = ArrayList<Song>()
    var onItemClick: ((Song) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        TrackBaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        tracks[position].also { song ->
            holder.bind(song)
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(song)
            }
        }
    }
}