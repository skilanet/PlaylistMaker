package com.example.playlistmaker.new_playlist.data.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.example.playlistmaker.new_playlist.domain.repository.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class FilesRepositoryImpl(private val context: Context) : FileRepository {
    override suspend fun saveToLocalStorage(uri: String, playlistName: String) {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")
        if (!filePath.exists()) filePath.mkdirs()
        val file = File(filePath, "$playlistName.jpg")
        val inputStream = context.contentResolver.openInputStream(Uri.parse(uri))
        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    override fun getFromLocalStorage(playlistName: String): Flow<String> = flow {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")
        val fileUri = File(filePath, "${playlistName}.jpg").toUri().toString()
        emit(fileUri)
    }
}