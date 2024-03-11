package com.example.playlistmaker.activities

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
import com.example.playlistmaker.databinding.ActivityFindBinding
import com.example.playlistmaker.findlogic.Debounce
import com.example.playlistmaker.findlogic.HistoryOfSearch
import com.example.playlistmaker.findlogic.OnItemClickListener
import com.example.playlistmaker.findlogic.SongApi
import com.example.playlistmaker.findlogic.SongDescription
import com.example.playlistmaker.findlogic.SongResponse
import com.example.playlistmaker.findlogic.SongsAdapter
import com.example.playlistmaker.objects.consts.Code
import com.example.playlistmaker.objects.consts.IntentKey
import com.example.playlistmaker.objects.consts.SharedPreference
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FindActivity : AppCompatActivity(), OnItemClickListener {

    private val baseUrl = "https://itunes.apple.com/"
    private val retrofit =
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()
    private val trackService = retrofit.create(SongApi::class.java)
    private val tracks = ArrayList<SongDescription>()
    private val adapter = SongsAdapter(this, true)
    private val history = ArrayList<SongDescription>()
    private val historyAdapter = SongsAdapter(this, false)
    private lateinit var binding: ActivityFindBinding
    private var gettedString: String = ""
    private val sharedPrefs by lazy {
        getSharedPreferences(SharedPreference.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
    }
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
        history.addAll(createListFromJson(sharedPrefs.getString(SharedPreference.ADD_HISTORY_LIST, "")!!).history)
        historyAdapter.tracks = history
        rvHistoryOfSearch.layoutManager = LinearLayoutManager(this)


        changeVisibility(Code.HIDE_ALL)
        binding.llHistoryOfSearch.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE

        findToolbar.setNavigationOnClickListener {
            finish()
        }

        fun sendRequest(trackName: String) = trackService.search(trackName)
            .enqueue(object : Callback<SongResponse> {
                override fun onResponse(
                    call: Call<SongResponse>, response: Response<SongResponse>
                ) {
                    changeVisibility(Code.HIDE_ALL)
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            changeVisibility(Code.SHOW_R)
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyItemRangeChanged(0, response.body()?.resultCount!!)
                        } else {
                            changeVisibility(Code.SHOW_NNF)
                        }
                    } else {
                        changeVisibility(Code.SHOW_NIC)
                    }
                }

                override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                    changeVisibility(Code.SHOW_NIC)
                }
            })

        etFindText.doOnTextChanged { text, _, _, count ->
            gettedString = text.toString()
            ivClear.visibility = setButtonVisibility(text)
            if (etFindText.hasFocus() && text?.isEmpty() == true){
                etFindText.isCursorVisible = etFindText.hasFocus()
                if (history.isEmpty()){
                    binding.llHistoryOfSearch.visibility = View.GONE
                } else{
                    binding.llHistoryOfSearch.visibility = View.VISIBLE
                }
                changeVisibility(Code.HIDE_ALL)
            } else
                binding.llHistoryOfSearch.visibility = View.GONE
            gettedString = text.toString()
            if (count >= 2) Debounce().searchDebounce {
                changeVisibility(Code.SHOW_PB)
                sendRequest(text.toString())
            }

        }

        etFindText.setOnFocusChangeListener { _, hasFocus ->
            etFindText.isCursorVisible = hasFocus
            if (hasFocus && etFindText.text.isEmpty()){
                if (history.isEmpty()){
                    binding.llHistoryOfSearch.visibility = View.GONE
                } else{
                    binding.llHistoryOfSearch.visibility = View.VISIBLE
                }
                changeVisibility(Code.HIDE_ALL)
            } else
                binding.llHistoryOfSearch.visibility = View.GONE
        }

        etFindText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etFindText.text.isNotEmpty()) {
                    changeVisibility(Code.SHOW_PB)
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
            changeVisibility(Code.HIDE_ALL)
        }

        binding.btnUpdate.setOnClickListener {
            changeVisibility(Code.SHOW_PB)
            sendRequest(gettedString)
        }

        binding.btnClearSearchHistory.setOnClickListener {
            sharedPrefs.edit()
                .clear()
                .apply()
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

    companion object {
        const val KEY = "ADD_KEY"
        const val DEFAULT = ""
    }

    private fun changeVisibility(
        code: Int
    ) {
        when (code) {
            Code.SHOW_R -> {
                rvFindShowTrack.visibility = View.VISIBLE
                llNothingNotFound.visibility = View.GONE
                llNoInternetConnection.visibility = View.GONE
                pbLoading.visibility = View.GONE
            }

            Code.SHOW_NNF -> {
                rvFindShowTrack.visibility = View.GONE
                llNothingNotFound.visibility = View.VISIBLE
                llNoInternetConnection.visibility = View.GONE
                pbLoading.visibility = View.GONE
            }

            Code.SHOW_NIC -> {
                rvFindShowTrack.visibility = View.GONE
                llNothingNotFound.visibility = View.GONE
                llNoInternetConnection.visibility = View.VISIBLE
                pbLoading.visibility = View.GONE
            }

            Code.HIDE_ALL -> {
                rvFindShowTrack.visibility = View.GONE
                llNothingNotFound.visibility = View.GONE
                llNoInternetConnection.visibility = View.GONE
                pbLoading.visibility = View.GONE
            }

            Code.SHOW_PB -> {
                rvFindShowTrack.visibility = View.GONE
                llNothingNotFound.visibility = View.GONE
                llNoInternetConnection.visibility = View.GONE
                pbLoading.visibility = View.VISIBLE
            }
        }
    }

    private fun createJsonFromList(historyOfSearch: HistoryOfSearch): String =
        Gson().toJson(historyOfSearch)

    private fun createListFromJson(json: String): HistoryOfSearch =
        if (json.isNotEmpty()) Gson().fromJson(json, HistoryOfSearch::class.java) else HistoryOfSearch(ArrayList())

    override fun onItemClick(position: Int, isSearch: Boolean) {
        if (Debounce().clickDebounce()) {
            if (!isSearch) {
                val sound = historyAdapter.tracks[position]
                Intent(this, MediaPlayerActivity::class.java).apply {
                    putExtra(IntentKey.INTENT_PLAYLIST_KEY, createJsonFromSong(sound))
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
            sharedPrefs.edit()
                .putString(
                    SharedPreference.ADD_HISTORY_LIST,
                    createJsonFromList(HistoryOfSearch(historyAdapter.tracks))
                )
                .apply()
            Intent(this, MediaPlayerActivity::class.java).apply {
                putExtra(IntentKey.INTENT_PLAYLIST_KEY, createJsonFromSong(sound))
                startActivity(this)
            }
        }
    }

    private fun createJsonFromSong(songDescription: SongDescription): String =
        Gson().toJson(songDescription)
}
