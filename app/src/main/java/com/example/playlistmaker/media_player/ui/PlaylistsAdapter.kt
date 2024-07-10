package com.example.playlistmaker.media_player.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistLinearBinding
import com.example.playlistmaker.media_library.domain.models.Playlist

class PlaylistsAdapter(private val context: Context) :
    RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {

    val playlists = ArrayList<Playlist>()
    var onItemClick: ((Playlist) -> Unit)? = null

    inner class ViewHolder(binding: ItemPlaylistLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val cover = binding.ivPlaylistImageView
        private val playlistName = binding.tvPlaylistName
        val countOfTracks = binding.tvCountOfSongs
        fun bind(playlist: Playlist) {
            if (playlist.uri.isNotEmpty()) {
                cover.setImageURI(Uri.parse(playlist.uri))
            } else cover.setImageResource(R.drawable.placeholder)
            playlistName.text = playlist.name
            countOfTracks.text =
                context.resources.getQuantityString(
                    R.plurals.tracks,
                    playlist.countOfTracks,
                    playlist.countOfTracks
                )
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPlaylistLinearBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            playlists[position].also { playlist ->
                bind(playlist)
                itemView.setOnClickListener {
                    onItemClick?.invoke(playlist)
                }
            }
        }
    }
}