package com.example.playlistmaker.presentation.find

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityFindBinding
import com.example.playlistmaker.domain.models.Song
import com.example.playlistmaker.domain.repository.DebounceInteractor
import com.example.playlistmaker.domain.repository.SongsUseCase
import com.example.playlistmaker.presentation.media_player.MediaPlayerActivity
import com.google.gson.Gson

class FindActivity : AppCompatActivity() {


    private companion object {
        const val KEY = "ADD_KEY"
        const val DEFAULT = ""
        const val SHOW_R = 0x000000
        const val SHOW_NNF = 0x000001
        const val SHOW_NIC = 0x000002
        const val HIDE_ALL = 0x000003
        const val SHOW_PB = 0x000004
        const val INTENT_PLAYLIST_KEY = "INTENT_PLAYLIST_KEY"
    }

    private val tracks = ArrayList<Song>()
    private var detailsRunnable: Runnable? = null
    private val adapter = SongsAdapter()
    private val history = ArrayList<Song>()
    private val historyAdapter = SongsAdapter()
    private lateinit var binding: ActivityFindBinding
    private var gettedString: String = ""
    private val sharedPreferenceInteractor = Creator.provideSharedPreferenceInteractor()
    private lateinit var debounceInteractor: DebounceInteractor
    private val songsUseCase = Creator.provideSongsUseCase(setBackground = { code: Int ->
        changeVisibility(code)
    })
    private lateinit var pbLoading: ProgressBar
    private lateinit var llNothingNotFound: LinearLayout
    private lateinit var llNoInternetConnection: LinearLayout
    private lateinit var rvFindShowTrack: RecyclerView
    private lateinit var rvHistoryOfSearch: RecyclerView
    val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
        adapter.onItemClick = { song -> onItemClick(song) }
        rvFindShowTrack.layoutManager = LinearLayoutManager(this)
        rvHistoryOfSearch.adapter = historyAdapter
        history.addAll(sharedPreferenceInteractor.getSongsFromSharedPreference())
        historyAdapter.tracks = history
        historyAdapter.onItemClick = { song -> onItemClick(song) }
        rvHistoryOfSearch.layoutManager = LinearLayoutManager(this)

        changeVisibility(HIDE_ALL)
        binding.llHistoryOfSearch.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE

        findToolbar.setNavigationOnClickListener {
            finish()
        }

        fun sendRequest(term: String) {
            songsUseCase.searchSongs(term = term, consumer = object : SongsUseCase.SongsConsumer {
                override fun consume(foundSongs: List<Song>) {
                    val currentRunnable = detailsRunnable
                    if (currentRunnable != null) {
                        handler.removeCallbacks(currentRunnable)
                    }
                    val newDetailsRunnable = Runnable {
                        tracks.addAll(foundSongs)
                        adapter.notifyItemRangeChanged(0, tracks.size)
                    }
                    detailsRunnable = newDetailsRunnable
                    handler.post(newDetailsRunnable)
                }
            })
        }

        etFindText.doOnTextChanged { text, _, _, count ->
            gettedString = text.toString()
            ivClear.visibility = setButtonVisibility(text)
            if (etFindText.hasFocus() && text?.isEmpty() == true) {
                etFindText.isCursorVisible = etFindText.hasFocus()
                if (history.isEmpty()) {
                    binding.llHistoryOfSearch.visibility = View.GONE
                } else {
                    binding.llHistoryOfSearch.visibility = View.VISIBLE
                }
                changeVisibility(HIDE_ALL)
            } else binding.llHistoryOfSearch.visibility = View.GONE
            gettedString = text.toString()
            if (count >= 2) debounceInteractor.searchDebounce {
                changeVisibility(SHOW_PB)
                sendRequest(gettedString)
            }

        }

        etFindText.setOnFocusChangeListener { _, hasFocus ->
            etFindText.isCursorVisible = hasFocus
            if (hasFocus && etFindText.text.isEmpty()) {
                if (history.isEmpty()) {
                    binding.llHistoryOfSearch.visibility = View.GONE
                } else {
                    binding.llHistoryOfSearch.visibility = View.VISIBLE
                }
                changeVisibility(HIDE_ALL)
            } else binding.llHistoryOfSearch.visibility = View.GONE
        }

        etFindText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etFindText.text.isNotEmpty()) {
                    changeVisibility(SHOW_PB)
                    sendRequest(etFindText.text.toString())
                }
            }
            false
        }

        ivClear.setOnClickListener {
            etFindText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            val tracksSize = adapter.itemCount
            tracks.clear()
            adapter.notifyItemRangeRemoved(0, tracksSize)
            changeVisibility(HIDE_ALL)
        }

        binding.btnUpdate.setOnClickListener {
            changeVisibility(SHOW_PB)
            sendRequest(gettedString)
        }

        binding.btnClearSearchHistory.setOnClickListener {
            sharedPreferenceInteractor.clearSharedPreference()
            val size = history.size
            history.clear()
            historyAdapter.notifyItemRangeRemoved(0, size)
            binding.llHistoryOfSearch.visibility = View.GONE
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

    private fun changeVisibility(
        code: Int
    ) {
        when (code) {
            SHOW_R -> {
                rvFindShowTrack.visibility = View.VISIBLE
                llNothingNotFound.visibility = View.GONE
                llNoInternetConnection.visibility = View.GONE
                pbLoading.visibility = View.GONE
            }

            SHOW_NNF -> {
                rvFindShowTrack.visibility = View.GONE
                llNothingNotFound.visibility = View.VISIBLE
                llNoInternetConnection.visibility = View.GONE
                pbLoading.visibility = View.GONE
            }

            SHOW_NIC -> {
                rvFindShowTrack.visibility = View.GONE
                llNothingNotFound.visibility = View.GONE
                llNoInternetConnection.visibility = View.VISIBLE
                pbLoading.visibility = View.GONE
            }

            HIDE_ALL -> {
                rvFindShowTrack.visibility = View.GONE
                llNothingNotFound.visibility = View.GONE
                llNoInternetConnection.visibility = View.GONE
                pbLoading.visibility = View.GONE
            }

            SHOW_PB -> {
                rvFindShowTrack.visibility = View.GONE
                llNothingNotFound.visibility = View.GONE
                llNoInternetConnection.visibility = View.GONE
                pbLoading.visibility = View.VISIBLE
            }
        }
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
            sharedPreferenceInteractor.setSongsToSharedPreference(historyAdapter.tracks)
            Intent(this, MediaPlayerActivity::class.java).apply {
                putExtra(INTENT_PLAYLIST_KEY, createJsonFromSong(song))
                startActivity(this)
            }
        }
    }

    private fun createJsonFromSong(songDescription: Song): String = Gson().toJson(songDescription)

    override fun onDestroy() {
        val currentRunnable = detailsRunnable
        if (currentRunnable != null) handler.removeCallbacks(currentRunnable)
        super.onDestroy()
    }
}
