package com.example.playlistmaker.findactivity

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R

class TrackAdapter(
    private val tracks : List<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        Log.d(RECYCLER_TEST_TAG, "ViewHolder creating started")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_base, parent, false)
        Log.d(RECYCLER_TEST_TAG, "ViewHolder creating finished")
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        Log.d(RECYCLER_TEST_TAG, "ViewHolder bind started")
        holder.bind(tracks[position])
        Log.d(RECYCLER_TEST_TAG, "ViewHolder creating finished")
    }
}