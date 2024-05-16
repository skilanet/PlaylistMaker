package com.example.playlistmaker.media_player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaPlayerBinding
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.media_player.presentation.view_model.MediaPlayerViewModel
import com.example.playlistmaker.media_player.ui.models.PlayingState
import com.example.playlistmaker.util.FragmentBinding
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaPlayerFragment : FragmentBinding<FragmentMediaPlayerBinding>() {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaPlayerBinding =
        FragmentMediaPlayerBinding.inflate(layoutInflater, container, false)

    companion object {
        const val SONG_TRANS_KEY = "song_trans_key"
        fun createArgs(string: String): Bundle = bundleOf(
            SONG_TRANS_KEY to string
        )
    }

    private lateinit var btnStartPause: ImageView
    private lateinit var tvNowTime: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val song = createSongFromJson(requireArguments().getString(SONG_TRANS_KEY)!!)
        val url = song.previewUrl
        btnStartPause = binding.ivStopPlayButton
        tvNowTime = binding.tvNowTime

        val viewModel: MediaPlayerViewModel by viewModel {
            parametersOf(url)
        }

        viewModel.observePlayingState().observe(viewLifecycleOwner) {
            setImage(it)
            viewModel.stateControl()
        }
        viewModel.observeTimeState().observe(viewLifecycleOwner) {
            binding.tvNowTime.text = it
            viewModel.updateTimeState()
        }

        // set info
        Glide.with(requireContext()).load(song.artworkUrl512)
            .into(binding.ivAlbumArtwork)
        binding.tvAlbumTextTop.text = song.collectionName
        binding.tvTrackAuthorTop.text = song.artistName
        binding.tvDurationText.text = song.currentTime
        binding.tvAlbumText.text = song.collectionName
        binding.tvYearText.text = song.currentDate
        binding.tvGenreText.text = song.primaryGenreName
        binding.tvCountryText.text = song.country


        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.onPrepare()
        btnStartPause.setOnClickListener {
            viewModel.playingControl()
        }

    }

    private fun createSongFromJson(json: String): Song = Gson().fromJson(json, Song::class.java)

    private fun setImage(state: PlayingState) {
        btnStartPause.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(), when (state) {
                    is PlayingState.Default,
                    PlayingState.Prepared,
                    PlayingState.Paused,
                    PlayingState.Complete -> R.drawable.play_image

                    is PlayingState.Playing -> R.drawable.pause_image
                }
            )
        )
    }

}