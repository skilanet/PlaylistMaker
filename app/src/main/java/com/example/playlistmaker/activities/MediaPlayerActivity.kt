package com.example.playlistmaker.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.findlogic.SongDescription
import com.example.playlistmaker.findlogic.fill
import com.example.playlistmaker.objects.consts.IntentKey
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val songString = intent.getStringExtra(IntentKey.INTENT_PLAYLIST_KEY)
        val song = createSongFromJson(songString!!)

        Glide.with(this)
            .load(song.artworkUrl512)
            .into(binding.ivAlbumArtwork)
        song.fill(binding.tvTrackNameTop, binding.tvTrackAuthorTop, binding.tvDurationText)
        binding.tvAlbumText.text = song.collectionName
        binding.tvYearText.text = SimpleDateFormat("YYYY", Locale.getDefault()).format(293000L)
        binding.tvGenreText.text = song.primaryGenreName
        binding.tvCountryText.text = song.country

        binding.ivBack.setOnClickListener {
            finish()
        }

    }

    private fun createSongFromJson(json: String): SongDescription =
        Gson().fromJson(json, SongDescription::class.java)

}