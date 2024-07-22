package com.example.playlistmaker.new_playlist.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.example.playlistmaker.new_playlist.presentation.AddPlaylistViewModel
import com.example.playlistmaker.new_playlist.ui.models.PlaylistEditState
import com.example.playlistmaker.playlist.ui.PlaylistFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var _id = 0
    private val viewModel by viewModel<AddPlaylistViewModel>()
    private var editFlag = false

    override fun setup() {
        binding.btnSavePlaylist.isEnabled = !binding.tietPlaylistName.text.isNullOrBlank()
        exitDialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
            .setTitle(requireContext().getString(R.string.cancel_playlist_creating))
            .setMessage(requireContext().getString(R.string.all_unsave_data_will_be_lost))
            .setNeutralButton(requireContext().getString(R.string.cancel)) { _, _ -> }
            .setNegativeButton(requireContext().getString(R.string.yes)) { _, _ ->
                closeFragment()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editFlag = arguments?.getBoolean(PlaylistFragment.EDIT_KEY) ?: false
        if (editFlag) {
            viewModel.observeEditState().observe(viewLifecycleOwner) {
                renderState(it)
            }
            binding.tvTitle.setText(R.string.edit)
            requireArguments().getInt(PlaylistFragment.EDIT_ID_KEY).also {
                viewModel.getPlaylist(it)
                _id = it
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.playlistStateFlow.filterNotNull().collectLatest {
                    withContext(Dispatchers.Main) {
                        _uri = Uri.parse(it.uri)
                        binding.ivPhotoInput.setImageURI(Uri.parse(it.uri))
                        binding.tietPlaylistName.setText(it.name)
                        binding.tietPlaylistDescription.setText(it.description ?: "")
                    }
                }
            }
        }
        binding.ivBack.setOnClickListener {
            chooseExitMode()
        }
        binding.tietPlaylistName.doOnTextChanged { text, _, _, _ ->
            binding.btnSavePlaylist.isEnabled = !text.isNullOrBlank()
        }
        binding.ivPhotoInput.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnSavePlaylist.setOnClickListener {
            if (imageFlag) createPlaylistIfImageEdited()
            else if (editFlag && _uri != Uri.EMPTY) createPlaylist(_uri.toString())
            else createPlaylist("")
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                chooseExitMode()
            }
        })
    }

    private fun createPlaylistIfImageEdited() {
        viewModel.putImageToLocalStorage(
            _uri.toString(),
            binding.tietPlaylistName.text.toString()
        )
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fileState.filter { it.isNotEmpty() }.collectLatest { uri ->
                createPlaylist(uri)
            }
        }
    }

    private fun createPlaylist(uri: String) = Playlist(
        id = _id,
        name = binding.tietPlaylistName.text.toString(),
        description = binding.tietPlaylistDescription.text.toString(),
        uri = uri,
        tracks = emptyList(),
        countOfTracks = 0
    ).also {
        if (editFlag) {
            viewModel.updatePlaylist(it)
        } else {
            viewModel.insertPlaylist(it)
            closeFragment()
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
        else closeFragment()
    }

    private fun closeFragment() {
        val args = arguments
        if (args != null && args.containsKey(MediaPlayerActivity.FRAGMENT_KEY)) {
            if (args.getInt(MediaPlayerActivity.FRAGMENT_KEY) == MediaPlayerActivity.FRAGMENT_CODE) {
                requireActivity().finish()
            } else findNavController().navigateUp()
        } else findNavController().navigateUp()
    }

    private fun renderState(state: PlaylistEditState) {
        when (state) {
            is PlaylistEditState.EditFinished -> closeFragment()
            else -> {}
        }
    }
}