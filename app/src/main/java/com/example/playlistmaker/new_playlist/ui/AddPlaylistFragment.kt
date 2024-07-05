package com.example.playlistmaker.new_playlist.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.new_playlist.domain.models.TracksInPlaylist
import com.example.playlistmaker.new_playlist.presentation.AddPlaylistViewModel
import com.example.playlistmaker.util.FragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AddPlaylistFragment : FragmentBinding<FragmentCreatePlaylistBinding>() {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePlaylistBinding =
        FragmentCreatePlaylistBinding.inflate(layoutInflater, container, false)

    private val playlist =
        Playlist(name = "", description = null, uri = Uri.EMPTY, tracks = TracksInPlaylist(
            arrayListOf()
        ), countOfTracks = 0)
    private lateinit var exitDialog: MaterialAlertDialogBuilder
    private var imageFlag = false
    private var savePressedFlag = false
    private val viewModel by viewModel<AddPlaylistViewModel>()

    override fun setup() {
        binding.btnSavePlaylist.isEnabled = !binding.tietPlaylistName.text.isNullOrBlank()
        exitDialog = MaterialAlertDialogBuilder(requireContext())
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
            playlist.name = binding.tietPlaylistName.text!!.toString()
            playlist.description = binding.tietPlaylistDescription.text?.toString() ?: ""
            if (imageFlag) {
                saveToLocalStorage(playlist.uri, playlist.name)
                playlist.uri = playlist.getUriFromPrivateStorage()
            }
            viewModel.clickListener(playlist)
            savePressedFlag = true
            findNavController().navigateUp()
        }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.ivPhotoInput.setImageURI(uri)
                playlist.uri = uri
                imageFlag = true
            }
        }

    private fun chooseExitMode() {
        if (savePressedFlag) findNavController().navigateUp()
        else exitDialog.show()
    }

    private fun Playlist.getUriFromPrivateStorage(): Uri {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")
        return File(filePath, "${this.name}.jpg").toUri()
    }
    private fun saveToLocalStorage(uri: Uri, playlistName: String) {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")
        if (!filePath.exists()) filePath.mkdirs()
        val file = File(filePath, "$playlistName.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

}