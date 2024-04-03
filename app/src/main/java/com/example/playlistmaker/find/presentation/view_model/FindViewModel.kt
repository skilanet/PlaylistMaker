package com.example.playlistmaker.find.presentation.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.find.domain.models.HistoryOfSearch
import com.example.playlistmaker.find.domain.models.Resource
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.find.domain.repository.SongsUseCase
import com.example.playlistmaker.find.ui.states.HistoryState
import com.example.playlistmaker.find.ui.states.TracksState
import com.example.playlistmaker.utils.Creator

private const val REQUEST_KEY = "REQUEST_KEY"

class FindViewModel(
    application: Application
) : ViewModel() {
    private val tracks = ArrayList<Song>()
    private var lastChangedText: String? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUESTS_TOKEN = Any()
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FindViewModel(this[APPLICATION_KEY] as App)
            }
        }
    }

    private val historySharedPreferenceInteractor =
        Creator.provideHistorySharedPreferenceInteractor(application)
    private val historyState = MutableLiveData<HistoryState>()
    init {
        val history = historySharedPreferenceInteractor.getSongsFromSharedPreference()
        if (history.history.isEmpty()) historyState.postValue(HistoryState.Empty)
        else historyState.postValue(HistoryState.Content(history.history))
    }

    fun updateHistoryState(history: List<Song>) {
        if (history.isEmpty()) {
            historyState.postValue(HistoryState.Empty)
            historySharedPreferenceInteractor.clearSharedPreference()
        }
        else {
            historyState.postValue(HistoryState.Content(history))
            historySharedPreferenceInteractor.setSongsToSharedPreference(HistoryOfSearch(history))
        }
    }

    fun observeHistoryState(): LiveData<HistoryState> = historyState
    private val handler = Handler(Looper.getMainLooper())
    private val searchState = MutableLiveData<TracksState>()
    private fun updateSearchState(state: TracksState) {
        searchState.postValue(state)
    }
    fun observeSearchState(): LiveData<TracksState> = searchState
    private val songUseCase = Creator.provideSongsUseCase()
    fun sendRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            updateSearchState(TracksState.Loading)
            songUseCase.searchSongs(searchText, object : SongsUseCase.SongsConsumer {
                override fun consume(foundSongs: Resource<List<Song>>) {
                    Log.d(REQUEST_KEY, foundSongs.toString())
                    when (foundSongs) {
                        is Resource.Success -> {
                            if (foundSongs.data.isEmpty()) updateSearchState(TracksState.NothingNotFound)
                            else {
                                tracks.clear()
                                tracks.addAll(foundSongs.data)
                                updateSearchState(TracksState.Content(tracks))
                            }
                        }

                        is Resource.Error -> updateSearchState(TracksState.Error(foundSongs.errorCode))
                    }
                }

            })
        }
    }
    fun searchDebounce(changedText: String) {
        if (lastChangedText == changedText) return
        lastChangedText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUESTS_TOKEN)
        val searchRunnable = { sendRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable, SEARCH_REQUESTS_TOKEN, postTime
        )

    }
    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUESTS_TOKEN)
    }
}