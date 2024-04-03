package com.example.playlistmaker.find.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ActivityFindBinding
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.find.domain.repository.DebounceInteractor
import com.example.playlistmaker.find.presentation.view_model.FindViewModel
import com.example.playlistmaker.find.ui.states.HistoryState
import com.example.playlistmaker.find.ui.states.TracksState
import com.example.playlistmaker.media_player.ui.MediaPlayerActivity
import com.example.playlistmaker.utils.Creator
import com.google.gson.Gson

class FindActivity : AppCompatActivity() {


    private companion object {
        const val KEY = "ADD_KEY"
        const val DEFAULT = ""
        const val INTENT_PLAYLIST_KEY = "INTENT_PLAYLIST_KEY"
    }

    private val adapter = SongsAdapter()
    private val historyAdapter = SongsAdapter()
    private lateinit var binding: ActivityFindBinding
    private var gettedString: String = ""
    private lateinit var debounceInteractor: DebounceInteractor
    private lateinit var pbLoading: ProgressBar
    private lateinit var llNothingNotFound: LinearLayout
    private lateinit var llNoInternetConnection: LinearLayout
    private lateinit var llHistoryOfSearch: LinearLayout
    private lateinit var rvFindShowTrack: RecyclerView
    private lateinit var viewModel: FindViewModel
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // viewmodel initialize
        viewModel = ViewModelProvider(
            this, FindViewModel.getViewModelFactory()
        )[FindViewModel::class.java]

        viewModel.observeSearchState().observe(this) {
            renderState(it)
        }
        viewModel.observeHistoryState().observe(this) {
            renderHistoryState(it)
        }

        // lateinit initialize
        debounceInteractor = Creator.provideDebounceRepository(handler)
        rvFindShowTrack = binding.rvFindShowTrack
        llNoInternetConnection = binding.llNoInternetConnection
        llNothingNotFound = binding.llNothingNotFound
        llHistoryOfSearch = binding.llHistoryOfSearch
        pbLoading = binding.pbLoading
        val findToolbar = binding.findToolbar
        val ivClear = binding.ivClear
        val etFindText = binding.etFindText

        // get string from savedInstance
        if (savedInstanceState != null) {
            gettedString = savedInstanceState.getString(KEY, DEFAULT).toString()
            etFindText.setText(gettedString)
        }

        // adapter initialize
        rvFindShowTrack.adapter = adapter
        adapter.onItemClick = { onItemClick(it) }
        rvFindShowTrack.layoutManager = LinearLayoutManager(this)

        binding.rvHistoryOfSearch.adapter = historyAdapter
        historyAdapter.onItemClick = { onItemClick(it) }
        binding.rvHistoryOfSearch.layoutManager = LinearLayoutManager(this)

        findToolbar.setNavigationOnClickListener {
            finish()
        }

        etFindText.doOnTextChanged { text, _, _, count ->
            gettedString = text.toString()
            ivClear.visibility = setButtonVisibility(text)
            if (etFindText.hasFocus() && text?.isEmpty() == true) {
                etFindText.isCursorVisible = etFindText.hasFocus()
                if (historyAdapter.tracks.isEmpty()) {
                    binding.llHistoryOfSearch.visibility = View.GONE
                } else {
                    binding.llHistoryOfSearch.visibility = View.VISIBLE
                }
                hideAll()
            } else binding.llHistoryOfSearch.visibility = View.GONE
            gettedString = text.toString()
            if (count >= 2) viewModel.searchDebounce(text.toString())

        }

        etFindText.setOnFocusChangeListener { _, hasFocus ->
            etFindText.isCursorVisible = hasFocus
            if (hasFocus && etFindText.text.isEmpty()) {
                if (historyAdapter.tracks.isEmpty()) {
                    binding.llHistoryOfSearch.visibility = View.GONE
                } else {
                    binding.llHistoryOfSearch.visibility = View.VISIBLE
                }
                hideAll()
            } else binding.llHistoryOfSearch.visibility = View.GONE
        }

        etFindText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etFindText.text.isNotEmpty()) {
                    viewModel.sendRequest(etFindText.text.toString())
                }
            }
            false
        }

        ivClear.setOnClickListener {
            etFindText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            val size = adapter.itemCount
            adapter.tracks.clear()
            adapter.notifyItemRangeChanged(0, size)
            hideAll()
        }

        binding.btnUpdate.setOnClickListener {
            viewModel.sendRequest(gettedString)
        }

        binding.btnClearSearchHistory.setOnClickListener {
            val size = historyAdapter.itemCount
            historyAdapter.tracks.clear()
            historyAdapter.notifyItemRangeChanged(0, size)
            viewModel.updateHistoryState(historyAdapter.tracks)
            hideAll()
        }

    }

    private fun setButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY, gettedString)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        gettedString = savedInstanceState.getString(KEY, DEFAULT)
    }

    private fun showRecycle(tracks: List<Song>) {
        hideAll()
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyItemRangeChanged(0, adapter.itemCount)
        rvFindShowTrack.visibility = View.VISIBLE
    }

    private fun showHistory(history: List<Song>) {
        hideAll()
        historyAdapter.tracks.clear()
        historyAdapter.tracks.addAll(history)
        historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
        llHistoryOfSearch.visibility = View.VISIBLE
    }

    private fun showNothingNotFount() {
        hideAll()
        llNothingNotFound.visibility = View.VISIBLE
    }

    private fun showNoInternetConnection() {
        hideAll()
        llNoInternetConnection.visibility = View.VISIBLE
    }

    private fun showLoading() {
        hideAll()
        pbLoading.visibility = View.VISIBLE
    }

    private fun hideAll() {
        rvFindShowTrack.visibility = View.GONE
        llNothingNotFound.visibility = View.GONE
        llNoInternetConnection.visibility = View.GONE
        pbLoading.visibility = View.GONE
        llHistoryOfSearch.visibility = View.GONE
    }

    private fun onItemClick(song: Song) {
        if (debounceInteractor.clickDebounce()) {
            val contains = historyAdapter.tracks.contains(song)
            updateHistoryAdapter(contains, song)
            if (historyAdapter.tracks.size > 9) {
                historyAdapter.tracks.removeAt(9)
            }
            viewModel.updateHistoryState(historyAdapter.tracks)
            historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
            Intent(this, MediaPlayerActivity::class.java).apply {
                putExtra(INTENT_PLAYLIST_KEY, createJsonFromSong(song))
                startActivity(this)
            }
        }
    }

    private fun updateHistoryAdapter(isContains: Boolean, song: Song) {
        if (isContains) historyAdapter.tracks.remove(song)
        historyAdapter.tracks.add(0, song)
    }

    private fun renderState(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showRecycle(state.tracks)
            is TracksState.Error -> showNoInternetConnection()
            is TracksState.NothingNotFound -> showNothingNotFount()

        }
    }

    private fun renderHistoryState(state: HistoryState) {
        when (state) {
            is HistoryState.Content -> showHistory(state.data)
            is HistoryState.Empty -> hideAll()
        }
    }

    private fun createJsonFromSong(song: Song): String = Gson().toJson(song)

}
