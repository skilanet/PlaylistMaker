package com.example.playlistmaker.find.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.find.domain.models.HistoryOfSearch
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.find.domain.repository.HistorySharedPreference
import com.example.playlistmaker.find.domain.repository.SongsInteractor
import com.example.playlistmaker.find.ui.states.HistoryState
import com.example.playlistmaker.find.ui.states.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FindViewModel(
    private val historySharedPreference: HistorySharedPreference,
    private val songsInteractor: SongsInteractor
) : ViewModel() {
    private var lastChangedText: String? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
    }

    private val historyState = MutableLiveData<HistoryState>()
    private var searchJob: Job? = null

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
    private val searchState = MutableLiveData<TracksState>()
    private fun updateSearchState(state: TracksState) {
        searchState.postValue(state)
    }

    fun observeSearchState(): LiveData<TracksState> = searchState
    fun sendRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            updateSearchState(TracksState.Loading)
            viewModelScope.launch {
                songsInteractor.searchSongs(searchText)
                    .collect { pair ->
                        renderRequest(pair.first, pair.second)
                    }
            }
        }
    }

    fun search(changedText: String, isWithDebounce: Boolean){
        if (lastChangedText == changedText) return
        lastChangedText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (isWithDebounce) delay(SEARCH_DEBOUNCE_DELAY)
            sendRequest(changedText)
        }
    }

    private fun renderRequest(songs: List<Song>?, code: Int) {
        val listOfSongs = ArrayList<Song>()
        if (songs != null) listOfSongs.addAll(songs)

        when {
            code != 200 -> updateSearchState(TracksState.Error)
            songs.isNullOrEmpty() -> updateSearchState(TracksState.NothingNotFound)
            else -> updateSearchState(TracksState.Content(listOfSongs))
        }
    }
}