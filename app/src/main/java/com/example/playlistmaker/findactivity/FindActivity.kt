package com.example.playlistmaker.findactivity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivityFindBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FindActivity : AppCompatActivity() {

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit =
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()
    private val trackService = retrofit.create(SongApi::class.java)
    private val tracks = ArrayList<SongDescription>()
    private val adapter = TrackAdapter()
    private lateinit var gettedString: String

    private lateinit var binding: ActivityFindBinding
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val recycler = binding.rvFindShowTrack
        val findToolbar = binding.findToolbar
        val ivClear = binding.ivClear
        val etFindText = binding.etFindText

        recycler.adapter = adapter
        adapter.tracks = tracks
        recycler.layoutManager = LinearLayoutManager(this)

        fun changeVisibility(code: Int) {
            when (code) {
                0 -> {
                    recycler.visibility = View.VISIBLE
                    binding.llNothingNotFound.visibility = View.GONE
                    binding.llNoInternetConnection.visibility = View.GONE
                }

                1 -> {
                    recycler.visibility = View.GONE
                    binding.llNothingNotFound.visibility = View.VISIBLE
                }

                2 -> {
                    recycler.visibility = View.GONE
                    binding.llNoInternetConnection.visibility = View.VISIBLE
                }
            }
        }

        findToolbar.setNavigationOnClickListener {
            finish()
        }

        etFindText.doOnTextChanged { text, _, _, _ ->
            ivClear.visibility = setButtonVisibility(text)
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
                            adapter.notifyDataSetChanged()
                        } else {
                            changeVisibility(1)

                        }
                    } else {
                        changeVisibility(2)
                        gettedString = trackName
                    }
                }

                override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                    changeVisibility(2)
                    gettedString = trackName
                }

            })

        etFindText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etFindText.text.isNotEmpty()) {
                    changeVisibility(0)
                    sendRequest(etFindText.text.toString())
                }
            }
            false
        }

        ivClear.setOnClickListener {
            etFindText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            tracks.clear()
            adapter.notifyDataSetChanged()
            changeVisibility(0)
        }

        binding.btnUpdate.setOnClickListener {
            changeVisibility(0)
            sendRequest(gettedString)
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
}
