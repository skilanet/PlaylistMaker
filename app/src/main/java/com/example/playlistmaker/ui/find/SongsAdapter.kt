package com.example.playlistmaker.ui.find

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackBaseBinding
import com.example.playlistmaker.domain.models.Song
import com.example.playlistmaker.findlogic.OnItemClickListener

class SongsAdapter(private val onItemClickListener: OnItemClickListener, private val isSearch: Boolean) : RecyclerView.Adapter<SongsViewHolder>() {


    var tracks = ArrayList<Song>()

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