package com.example.playlistmaker.ui.find

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.playlistmaker.findlogic.Debounce
import com.example.playlistmaker.findlogic.OnItemClickListener
import com.example.playlistmaker.ui.media_player.MediaPlayerActivity
import com.google.gson.Gson

class FindActivity : AppCompatActivity(), OnItemClickListener {


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
    private val adapter = SongsAdapter(this, true)
    private val history = ArrayList<Song>()
    private val historyAdapter = SongsAdapter(this, false)
    private lateinit var binding: ActivityFindBinding
    private var gettedString: String = ""
    private val sharedPreferenceInteractor = Creator.provideSharedPreferenceInteractor()
    private lateinit var pbLoading: ProgressBar
    private lateinit var llNothingNotFound: LinearLayout
    private lateinit var llNoInternetConnection: LinearLayout
    private lateinit var rvFindShowTrack: RecyclerView
    private lateinit var rvHistoryOfSearch: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


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
        rvFindShowTrack.layoutManager = LinearLayoutManager(this)

        rvHistoryOfSearch.adapter = historyAdapter
        history.addAll(sharedPreferenceInteractor.getSongsFromSharedPreference())
        historyAdapter.tracks = history
        rvHistoryOfSearch.layoutManager = LinearLayoutManager(this)


        changeVisibility(HIDE_ALL)
        binding.llHistoryOfSearch.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE

        findToolbar.setNavigationOnClickListener {
            finish()
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
            if (count >= 2) Debounce().searchDebounce {
                changeVisibility(SHOW_PB)
                Creator.provideSongsUseCase()
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
                    Creator.provideSongsUseCase()
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
            //sendRequest(gettedString)
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

    override fun onItemClick(position: Int, isSearch: Boolean) {
        if (Debounce().clickDebounce()) {
            if (!isSearch) {
                val sound = historyAdapter.tracks[position]
                Intent(this, MediaPlayerActivity::class.java).apply {
                    putExtra(INTENT_PLAYLIST_KEY, createJsonFromSong(sound))
                    startActivity(this)
                }
                return
            }
            val sound = adapter.tracks[position]
            if (historyAdapter.tracks.size > 9) {
                historyAdapter.tracks.removeAt(9)
                if (historyAdapter.tracks.contains(sound)) {
                    historyAdapter.tracks.remove(sound)
                    historyAdapter.tracks.add(0, sound)
                } else {
                    historyAdapter.tracks.add(0, sound)
                }
            } else {
                if (historyAdapter.tracks.contains(sound)) {
                    historyAdapter.tracks.remove(sound)
                    historyAdapter.tracks.add(0, sound)
                } else {
                    historyAdapter.tracks.add(0, sound)
                }
            }
            historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
            sharedPreferenceInteractor.setSongsToSharedPreference(historyAdapter.tracks)
            Intent(this, MediaPlayerActivity::class.java).apply {
                putExtra(INTENT_PLAYLIST_KEY, createJsonFromSong(sound))
                startActivity(this)
            }
        }
    }

    private fun createJsonFromSong(songDescription: Song): String = Gson().toJson(songDescription)
}
