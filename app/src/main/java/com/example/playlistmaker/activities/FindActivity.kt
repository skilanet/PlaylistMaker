package com.example.playlistmaker.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivityFindBinding
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

    private val baseUrl = "https://itunes.apple.com"
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val rvFindShowTrack = binding.rvFindShowTrack
        val findToolbar = binding.findToolbar
        val ivClear = binding.ivClear
        val etFindText = binding.etFindText
        val rvHistoryOfSearch = binding.rvHistoryOfSearch

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

        val layouts = Layouts(
            binding.rvFindShowTrack,
            binding.llNothingNotFound,
            binding.llNoInternetConnection
        )

        changeVisibility(
            layouts,
            Code.HIDE_ALL
        )
        binding.llHistoryOfSearch.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE

        findToolbar.setNavigationOnClickListener {
            finish()
        }

        fun sendRequest(trackName: String) = trackService.search(trackName)
            .enqueue(object : Callback<SongResponse> {
                override fun onResponse(
                    call: Call<SongResponse>, response: Response<SongResponse>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyItemRangeChanged(0, response.body()?.resultCount!!)
                        } else {
                            changeVisibility(
                                layouts,
                                Code.UNHIDE_NNF
                            )

                        }
                    } else {
                        changeVisibility(
                            layouts,
                            Code.UNHIDE_NIC
                        )
                    }
                }

                override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                    changeVisibility(
                        layouts,
                        Code.UNHIDE_NIC
                    )
                }

            })


        etFindText.doOnTextChanged { text, _, _, _ ->
            gettedString = text.toString()
            ivClear.visibility = setButtonVisibility(text)
            if (etFindText.hasFocus() && text?.isEmpty() == true){
                if (history.isEmpty()){
                    binding.llHistoryOfSearch.visibility = View.GONE
                } else{
                    binding.llHistoryOfSearch.visibility = View.VISIBLE
                }
                changeVisibility(layouts, Code.HIDE_ALL)
            } else
                binding.llHistoryOfSearch.visibility = View.GONE
            gettedString = text.toString()

        }

        etFindText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && etFindText.text.isEmpty()){
                if (history.isEmpty()){
                    binding.llHistoryOfSearch.visibility = View.GONE
                } else{
                    binding.llHistoryOfSearch.visibility = View.VISIBLE
                }
                changeVisibility(layouts, Code.HIDE_ALL)
            } else
                binding.llHistoryOfSearch.visibility = View.GONE
        }

        etFindText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etFindText.text.isNotEmpty()) {
                    changeVisibility(
                        layouts,
                        Code.UNHIDE_R
                    )
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
            changeVisibility(layouts, Code.HIDE_ALL)
        }

        binding.btnUpdate.setOnClickListener {
            changeVisibility(layouts, Code.UNHIDE_R)
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
        layouts: Layouts,
        code: Int
    ) {
        when (code) {
            Code.UNHIDE_R -> {
                layouts.rvFindShowTrack.visibility = View.VISIBLE
                layouts.llNothingNotFound.visibility = View.GONE
                layouts.llNoInternetConnection.visibility = View.GONE
            }

            Code.UNHIDE_NNF -> {
                layouts.rvFindShowTrack.visibility = View.GONE
                layouts.llNothingNotFound.visibility = View.VISIBLE
                layouts.llNoInternetConnection.visibility = View.GONE
            }

            Code.UNHIDE_NIC -> {
                layouts.rvFindShowTrack.visibility = View.GONE
                layouts.llNothingNotFound.visibility = View.GONE
                layouts.llNoInternetConnection.visibility = View.VISIBLE
            }

            Code.HIDE_ALL -> {
                layouts.rvFindShowTrack.visibility = View.GONE
                layouts.llNothingNotFound.visibility = View.GONE
                layouts.llNoInternetConnection.visibility = View.GONE
            }
        }
    }

    private fun createJsonFromList(historyOfSearch: HistoryOfSearch): String =
        Gson().toJson(historyOfSearch)

    private fun createListFromJson(json: String): HistoryOfSearch =
        if (json.isNotEmpty()) Gson().fromJson(json, HistoryOfSearch::class.java) else HistoryOfSearch(ArrayList())

    override fun onItemClick(position: Int, isSearch: Boolean) {
        if (!isSearch) {
            val sound = historyAdapter.tracks[position]
            Intent(this, MediaPlayerActivity::class.java).apply {
                putExtra(IntentKey.INTENT_PLAYLIST_KEY, createJsonFromSong(sound))
                startActivity(this)
            }
            return
        }
        val sound = adapter.tracks[position]
        if (historyAdapter.tracks.size > 9){
            historyAdapter.tracks.removeAt(9)
            if (historyAdapter.tracks.contains(sound)) {
                historyAdapter.tracks.remove(sound)
                historyAdapter.tracks.add(0, sound)
            } else {
                historyAdapter.tracks.add(0, sound)
            }
        } else{
            if (historyAdapter.tracks.contains(sound)) {
                historyAdapter.tracks.remove(sound)
                historyAdapter.tracks.add(0, sound)
            } else {
                historyAdapter.tracks.add(0, sound)
            }
        }
        historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
        sharedPrefs.edit()
            .putString(SharedPreference.ADD_HISTORY_LIST, createJsonFromList(HistoryOfSearch(historyAdapter.tracks)))
            .apply()
        Intent(this, MediaPlayerActivity::class.java).apply {
            putExtra(IntentKey.INTENT_PLAYLIST_KEY, createJsonFromSong(sound))
            startActivity(this)
        }
    }

    private fun createJsonFromSong(songDescription: SongDescription): String =
        Gson().toJson(songDescription)
}

//Data Class для сокращения кода при использовании метода changeVisibility
private data class Layouts(
    val rvFindShowTrack: View,
    val llNothingNotFound: LinearLayout,
    val llNoInternetConnection: LinearLayout
)
