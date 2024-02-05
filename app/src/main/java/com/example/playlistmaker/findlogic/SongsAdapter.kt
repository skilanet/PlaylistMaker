package com.example.playlistmaker.findlogic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackBaseBinding

class SongsAdapter(private val onItemClickListener: OnItemClickListener, private val isSearch: Boolean) : RecyclerView.Adapter<SongsViewHolder>() {

    var tracks = ArrayList<SongDescription>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder =
        SongsViewHolder(
            TrackBaseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemClickListener, isSearch
        )

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        holder.bind(tracks[position])
    }
}