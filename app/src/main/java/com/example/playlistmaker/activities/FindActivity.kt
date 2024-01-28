package com.example.playlistmaker.activities

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ActivityFindBinding
import com.example.playlistmaker.findlogic.HistoryOfSearch
import com.example.playlistmaker.findlogic.OnItemClickListener
import com.example.playlistmaker.findlogic.SongApi
import com.example.playlistmaker.findlogic.SongDescription
import com.example.playlistmaker.findlogic.SongResponse
import com.example.playlistmaker.findlogic.SongsAdapter
import com.example.playlistmaker.objects.Const.ADD_HISTORY_LIST
import com.example.playlistmaker.objects.Const.SHARED_PREFERENCE_NAME
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
    private val adapter = SongsAdapter(this)
    private val history = ArrayList<SongDescription>()
    private val historyAdapter = SongsAdapter(this)
    private lateinit var gettedString: String
    private lateinit var binding: ActivityFindBinding
    private val sharedPrefs by lazy {
        getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)
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

        rvFindShowTrack.adapter = adapter
        adapter.tracks = tracks
        rvFindShowTrack.layoutManager = LinearLayoutManager(this)

        rvHistoryOfSearch.adapter = historyAdapter
        history.addAll(createListFromJson(sharedPrefs.getString(ADD_HISTORY_LIST, "")!!).history)
        historyAdapter.tracks = history
        rvHistoryOfSearch.layoutManager = LinearLayoutManager(this)

        binding.rvFindShowTrack.visibility = View.GONE
        binding.llNothingNotFound.visibility = View.GONE
        binding.llNoInternetConnection.visibility = View.GONE
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
                                rvFindShowTrack,
                                binding.llNothingNotFound,
                                binding.llNoInternetConnection,
                                1
                            )

                        }
                    } else {
                        changeVisibility(
                            rvFindShowTrack,
                            binding.llNothingNotFound,
                            binding.llNoInternetConnection,
                            2
                        )
                        gettedString = trackName
                    }
                }

                override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                    changeVisibility(
                        rvFindShowTrack,
                        binding.llNothingNotFound,
                        binding.llNoInternetConnection,
                        2
                    )
                    gettedString = trackName
                }

            })

//        etFindText.setText(savedInstanceState?.getString(KEY, DEFAULT))

        etFindText.doOnTextChanged { text, _, _, _ ->
            gettedString = text.toString()
            ivClear.visibility = setButtonVisibility(text)
            if (etFindText.hasFocus() && text?.isEmpty() == true){
                if (history.isEmpty()){
                    binding.llHistoryOfSearch.visibility = View.GONE
                } else{
                    binding.llHistoryOfSearch.visibility = View.VISIBLE
                }
                changeVisibility(rvFindShowTrack, binding.llNothingNotFound, binding.llNoInternetConnection, 3)
            } else
                binding.llHistoryOfSearch.visibility = View.GONE

        }

        etFindText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && etFindText.text.isEmpty()){
                if (history.isEmpty()){
                    binding.llHistoryOfSearch.visibility = View.GONE
                } else{
                    binding.llHistoryOfSearch.visibility = View.VISIBLE
                }
                changeVisibility(rvFindShowTrack, binding.llNothingNotFound, binding.llNoInternetConnection, 3)
            } else
                binding.llHistoryOfSearch.visibility = View.GONE
        }

        etFindText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etFindText.text.isNotEmpty()) {
                    changeVisibility(
                        rvFindShowTrack,
                        binding.llNothingNotFound,
                        binding.llNoInternetConnection,
                        0
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
            changeVisibility(rvFindShowTrack, binding.llNothingNotFound, binding.llNoInternetConnection, 3)
        }
        binding.btnUpdate.setOnClickListener {
            changeVisibility(rvFindShowTrack, binding.llNothingNotFound, binding.llNoInternetConnection, 0)
            sendRequest(gettedString)
        }

        val listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key === ADD_HISTORY_LIST) {
                val history = sharedPreferences?.getString(ADD_HISTORY_LIST, null)
                if (history != null) {
                    historyAdapter.notifyItemRangeChanged(0, history.length)
                }
            }
        }
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

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
        const val DEFAULT = "DEFAULT"
    }

    private fun changeVisibility(
        recycler: RecyclerView,
        llNothingNotFound: LinearLayout,
        llNoInternetConnection: LinearLayout,
        code: Int
    ) {
        when (code) {
            0 -> {
                recycler.visibility = View.VISIBLE
                llNothingNotFound.visibility = View.GONE
                llNoInternetConnection.visibility = View.GONE
            }

            1 -> {
                recycler.visibility = View.GONE
                llNothingNotFound.visibility = View.VISIBLE
            }

            2 -> {
                recycler.visibility = View.GONE
                llNoInternetConnection.visibility = View.VISIBLE
            }

            3 -> {
                recycler.visibility = View.GONE
                llNothingNotFound.visibility = View.GONE
                llNoInternetConnection.visibility = View.GONE
            }
        }
    }

    private fun createJsonFromList(historyOfSearch: HistoryOfSearch): String =
        Gson().toJson(historyOfSearch)

    private fun createListFromJson(json: String): HistoryOfSearch =
        if (json.isNotEmpty()) Gson().fromJson(json, HistoryOfSearch::class.java) else HistoryOfSearch(ArrayList())

    override fun onItemClick(position: Int) {
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
        sharedPrefs.edit()
            .putString(ADD_HISTORY_LIST, createJsonFromList(HistoryOfSearch(historyAdapter.tracks)))
            .apply()
        Toast.makeText(this@FindActivity, "Track ${sound.trackName} добавлен в историю", Toast.LENGTH_SHORT).show()
    }
}
