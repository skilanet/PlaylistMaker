package com.example.playlistmaker.find.presentation.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.find.domain.models.HistoryOfSearch
import com.example.playlistmaker.find.domain.models.Resource
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.find.domain.repository.HistorySharedPreference
import com.example.playlistmaker.find.domain.repository.SongsInteractor
import com.example.playlistmaker.find.ui.states.HistoryState
import com.example.playlistmaker.find.ui.states.TracksState

class FindViewModel(
    private val historySharedPreference: HistorySharedPreference,
    private val songsInteractor: SongsInteractor
) : ViewModel() {
    private val tracks = ArrayList<Song>()
    private var lastChangedText: String? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUESTS_TOKEN = Any()
    }

    private val historyState = MutableLiveData<HistoryState>()

    init {
        val history = historySharedPreference.getSongsFromSharedPreference()
        if (history.history.isEmpty()) historyState.postValue(HistoryState.Empty)
        else historyState.postValue(HistoryState.Content(history.history))
    }

    fun updateHistoryState(history: ArrayList<Song>) {
        if (history.isEmpty()) {
            historyState.postValue(HistoryState.Empty)
            historySharedPreference.clearSharedPreference()
        } else {
            historyState.postValue(HistoryState.Content(history))
            historySharedPreference.setSongsToSharedPreference(HistoryOfSearch(history))
        }
    }

    fun observeHistoryState(): LiveData<HistoryState> = historyState
    private val handler = Handler(Looper.getMainLooper())
    private val searchState = MutableLiveData<TracksState>()
    private fun updateSearchState(state: TracksState) {
        searchState.postValue(state)
    }

    fun observeSearchState(): LiveData<TracksState> = searchState
    fun sendRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            updateSearchState(TracksState.Loading)
            songsInteractor.searchSongs(searchText, object : SongsInteractor.SongsConsumer {
                override fun consume(foundSongs: Resource<List<Song>>) {
                    when (foundSongs) {
                        is Resource.Success -> {
                            if (foundSongs.data.isEmpty()) updateSearchState(TracksState.NothingNotFound)
                            else {
                                tracks.clear()
                                tracks.addAll(foundSongs.data)
                                updateSearchState(TracksState.Content(tracks))
                            }
                        }

                        is Resource.Error -> updateSearchState(TracksState.Error)
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