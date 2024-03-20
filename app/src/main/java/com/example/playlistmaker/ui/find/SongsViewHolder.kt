package com.example.playlistmaker.ui.find

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackBaseBinding
import com.example.playlistmaker.Utils
import com.example.playlistmaker.domain.models.Song
import com.example.playlistmaker.findlogic.OnItemClickListener
import java.text.SimpleDateFormat
import java.util.Locale


class SongsViewHolder(
    private val binding: TrackBaseBinding,
    private val onItemClickListener: OnItemClickListener,
    private val isSearch: Boolean
) : RecyclerView.ViewHolder(
    binding.root
), View.OnClickListener {
    private val image: ImageView = binding.ivFindTrackImage
    private val trackName: TextView = binding.tvTrackName
    private val trackAuthor: TextView = binding.tvTrackAuthor
    private val trackTime: TextView = binding.tvTrackTime

    fun bind(model: Song) {
        Glide.with(binding.root.context).load(model.artworkUrl100).fitCenter()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(Utils.pxtodp(2f, itemView.context))).into(image)
        trackName.text = model.trackName
        trackAuthor.text = model.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L)
        binding.root.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        onItemClickListener.onItemClick(adapterPosition, isSearch)
    }
}