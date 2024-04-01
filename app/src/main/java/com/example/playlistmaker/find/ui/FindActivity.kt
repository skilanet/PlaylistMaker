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
import com.example.playlistmaker.find.domain.models.HistoryOfSearch
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.find.domain.repository.DebounceInteractor
import com.example.playlistmaker.find.domain.repository.HistorySharedPreferenceInteractor
import com.example.playlistmaker.find.view_model.FindViewModel
import com.example.playlistmaker.find.view_model.HistoryState
import com.example.playlistmaker.find.view_model.TracksState
import com.example.playlistmaker.media_player.ui.MediaPlayerActivity
import com.example.playlistmaker.utils.Creator
import com.google.gson.Gson

class FindActivity : AppCompatActivity() {


    private companion object {
        const val KEY = "ADD_KEY"
        const val DEFAULT = ""
        const val INTENT_PLAYLIST_KEY = "INTENT_PLAYLIST_KEY"
    }

    private val tracks = ArrayList<Song>()
    private var detailsRunnable: Runnable? = null
    private val adapter = SongsAdapter()
    private val history = ArrayList<Song>()
    private val historyAdapter = SongsAdapter()
    private lateinit var binding: ActivityFindBinding
    private var gettedString: String = ""
    private lateinit var debounceInteractor: DebounceInteractor
    private lateinit var pbLoading: ProgressBar
    private lateinit var llNothingNotFound: LinearLayout
    private lateinit var llNoInternetConnection: LinearLayout
    private lateinit var rvFindShowTrack: RecyclerView
    private lateinit var rvHistoryOfSearch: RecyclerView
    private lateinit var viewModel: FindViewModel
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedPreferenceInteractor: HistorySharedPreferenceInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferenceInteractor = Creator.provideHistorySharedPreferenceInteractor(this)

        viewModel = ViewModelProvider(
            this, FindViewModel.getViewModelFactory()
        )[FindViewModel::class.java]
        viewModel.observeSearchState().observe(this) {
            renderState(it)
        }
        viewModel.observeHistoryState().observe(this) {
            history.addAll(it.history)
        }

        debounceInteractor = Creator.provideDebounceRepository(handler)
        rvFindShowTrack = binding.rvFindShowTrack
        rvHistoryOfSearch = binding.rvHistoryOfSearch
        llNoInternetConnection = binding.llNoInternetConnection
        llNothingNotFound = binding.llNothingNotFound
        val findToolbar = binding.findToolbar
        val ivClear = binding.ivClear
        val etFindText = binding.etFindText
        pbLoading = binding.pbLoading

        if (savedInstanceState != null) {
            gettedString = savedInstanceState.getString(KEY, DEFAULT).toString()
            etFindText.setText(gettedString)
        }

        rvFindShowTrack.adapter = adapter
        adapter.tracks = tracks
        adapter.onItemClick = { onItemClick(it) }
        rvFindShowTrack.layoutManager = LinearLayoutManager(this)

        rvHistoryOfSearch.adapter = historyAdapter
        historyAdapter.tracks = history
        historyAdapter.onItemClick = { onItemClick(it) }
        rvHistoryOfSearch.layoutManager = LinearLayoutManager(this)

        hideAll()
        binding.llHistoryOfSearch.visibility = if (historyAdapter.tracks.isEmpty()) View.GONE else View.VISIBLE

        findToolbar.setNavigationOnClickListener {
            finish()
        }

        etFindText.doOnTextChanged { text, _, _, count ->
            gettedString = text.toString()
            ivClear.visibility = setButtonVisibility(text)
            if (etFindText.hasFocus() && text?.isEmpty() == true) {
                etFindText.isCursorVisible = etFindText.hasFocus()
                if (history.isEmpty()) {
                    changeHistoryVisibility(false)
                } else {
                    changeHistoryVisibility(true)
                }
                hideAll()
            } else changeHistoryVisibility(false)
            gettedString = text.toString()
            if (count >= 2) {
                viewModel.searchDebounce(text.toString())
            }
        }

        etFindText.setOnFocusChangeListener { _, hasFocus ->
            etFindText.isCursorVisible = hasFocus
            if (hasFocus && etFindText.text.isEmpty()) {
                if (history.isEmpty()) {
                    changeHistoryVisibility(false)
                } else {
                    changeHistoryVisibility(true)
                }
                hideAll()
            } else changeHistoryVisibility(false)
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
            viewModel.updateHistoryState(isClear = true)
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
    }

    private fun showHistory(history: List<Song>){
        hideAll()
        binding.llHistoryOfSearch.visibility = View.VISIBLE
    }

    private fun onItemClick(song: Song) {
        if (debounceInteractor.clickDebounce()) {
            if (historyAdapter.tracks.size > 9) {
                historyAdapter.tracks.removeAt(9)
                if (historyAdapter.tracks.contains(song)) {
                    historyAdapter.tracks.remove(song)
                    historyAdapter.tracks.add(0, song)
                } else {
                    historyAdapter.tracks.add(0, song)
                }
            } else {
                if (historyAdapter.tracks.contains(song)) {
                    historyAdapter.tracks.remove(song)
                    historyAdapter.tracks.add(0, song)
                } else {
                    historyAdapter.tracks.add(0, song)
                }
            }
            historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
            viewModel.updateHistoryState(HistoryOfSearch(historyAdapter.tracks), isClear = false)
            Intent(this, MediaPlayerActivity::class.java).apply {
                putExtra(INTENT_PLAYLIST_KEY, createJsonFromSong(song))
                startActivity(this)
            }
        }
    }

    private fun renderState(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showRecycle(state.tracks)
            is TracksState.Error -> showNoInternetConnection()
            is TracksState.NothingNotFound -> showNothingNotFount()

        }
    }

    private fun renderHistoryState(state: HistoryState){
        when (state){
            is HistoryState.Content -> showHistory()
        }
    }

    private fun createJsonFromSong(song: Song): String = Gson().toJson(song)

    override fun onDestroy() {
        val currentRunnable = detailsRunnable
        if (currentRunnable != null) handler.removeCallbacks(currentRunnable)
        super.onDestroy()
    }
}
