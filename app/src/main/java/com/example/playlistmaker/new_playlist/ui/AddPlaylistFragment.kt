package com.example.playlistmaker.new_playlist.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.core.FragmentBinding
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_player.ui.MediaPlayerActivity
import com.example.playlistmaker.new_playlist.domain.models.TracksInPlaylist
import com.example.playlistmaker.new_playlist.presentation.AddPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddPlaylistFragment : FragmentBinding<FragmentCreatePlaylistBinding>() {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePlaylistBinding =
        FragmentCreatePlaylistBinding.inflate(layoutInflater, container, false)

    private lateinit var exitDialog: MaterialAlertDialogBuilder
    private var imageFlag = false
    private var _uri: Uri = Uri.EMPTY
    private val viewModel by viewModel<AddPlaylistViewModel>()

    override fun setup() {
        binding.btnSavePlaylist.isEnabled = !binding.tietPlaylistName.text.isNullOrBlank()
        exitDialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
            .setTitle(requireContext().getString(R.string.cancel_playlist_creating))
            .setMessage(requireContext().getString(R.string.all_unsave_data_will_be_lost))
            .setNeutralButton(requireContext().getString(R.string.cancel)) { _, _ -> }
            .setNegativeButton(requireContext().getString(R.string.yes)) { _, _ ->
                findNavController().navigateUp()
            }
        binding.ivBack.setOnClickListener {
            chooseExitMode()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observePlaylistState().observe(viewLifecycleOwner) {}
        binding.tietPlaylistName.doOnTextChanged { text, _, _, _ ->
            binding.btnSavePlaylist.isEnabled = !text.isNullOrBlank()
            viewModel.checkTrack(text.toString())
        }
        binding.ivPhotoInput.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnSavePlaylist.setOnClickListener {
            viewModel.putImageToLocalStorage(
                _uri.toString(),
                binding.tietPlaylistName.text.toString()
            )
            lifecycleScope.launch {
                viewModel.fileState.collect { strUri ->
                    val playlist = Playlist(
                        name = binding.tietPlaylistName.text.toString(),
                        description = binding.tietPlaylistDescription.text.toString(),
                        uri = strUri,
                        tracks = TracksInPlaylist(ArrayList()),
                        countOfTracks = 0
                    )
                    viewModel.clickListener(playlist)
                }
            }
            findNavController().navigateUp()
        }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.ivPhotoInput.setImageURI(uri)
                _uri = uri
                imageFlag = true
            }
        }

    private fun chooseExitMode() {
        if (imageFlag || !binding.tietPlaylistName.text.isNullOrBlank() || !binding.tietPlaylistDescription.text.isNullOrBlank()) exitDialog.show()
        else if (requireArguments().getInt(MediaPlayerActivity.FRAGMENT_KEY) == MediaPlayerActivity.FRAGMENT_CODE) requireActivity().finish()
        else findNavController().navigateUp()
    }
}