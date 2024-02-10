package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.objects.Utils
import com.example.playlistmaker.objects.consts.IntentKey
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val songString = intent.getStringExtra(IntentKey.INTENT_PLAYLIST_KEY)
        val song = Utils.createSongFromJson(songString!!)

        Glide.with(this)
            .load(song.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .into(binding.ivAlbumArtwork)
        binding.tvAlbumTextTop.text = song.collectionName
        binding.tvTrackAuthorTop.text = song.artistName
        binding.tvDurationText.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(293000L)
        binding.tvAlbumText.text = song.collectionName
        binding.tvYearText.text = SimpleDateFormat("YYYY", Locale.getDefault()).format(293000L)
        binding.tvGenreText.text = song.primaryGenreName
        binding.tvCountryText.text = song.country

        binding.ivBack.setOnClickListener {
            finish()
        }

    }
}