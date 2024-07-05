package com.example.playlistmaker.media_library.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistGridBinding
import com.example.playlistmaker.media_library.domain.models.Playlist

class PlaylistsAdapter : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {

    val playlists: ArrayList<Playlist> = arrayListOf()
    val onItemClick: (() -> Unit)? = null

    inner class ViewHolder(binding: ItemPlaylistGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val cover = binding.ivPlaylistCover
        private val playListName = binding.tvPlaylistName
        private val countOfTracks = binding.tvCountOfSongs
        fun bind(playlist: Playlist) {
            if (playlist.uri != Uri.EMPTY) {
                cover.setImageURI(playlist.uri)
            }
            else cover.setImageResource(R.drawable.placeholder)
            playListName.text = playlist.name
            val string = StringBuilder()
            countOfTracks.text = if (playlist.countOfTracks == 1) string.append("1 трек").toString()
            else string.append("${playlist.countOfTracks} треков").toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPlaylistGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            bind(playlist = playlists[position])
            itemView.setOnClickListener {
                onItemClick?.invoke()
            }
        }
    }
}