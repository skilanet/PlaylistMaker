package com.example.playlistmaker.findactivity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
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

    private val LOG_TAG = "REQUEST LOG"

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

        findToolbar.setNavigationOnClickListener {
            finish()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ivClear.visibility = setButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {}

        }

        etFindText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d(LOG_TAG, "Button was pressed")
                if (etFindText.text.isNotEmpty()) {
                    Log.d(LOG_TAG, "Text is not null (${etFindText.text})")
                    trackService.search(etFindText.text.toString())
                        .enqueue(object : Callback<SongResponse> {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onResponse(
                                call: Call<SongResponse>, response: Response<SongResponse>
                            ) {
                                Log.d(LOG_TAG, "Request complete with code: ${response.code()}")
                                if (response.code() == 200) {
                                    tracks.clear()
                                    if (response.body()?.results?.isNotEmpty() == true) {
                                        tracks.addAll(response.body()?.results!!)
                                        Log.d(LOG_TAG, tracks.toString())
                                        adapter.notifyDataSetChanged()
                                    }
                                    if (tracks.isEmpty()) {
                                        recycler.visibility = View.GONE
                                        binding.llNothingNotFound.visibility = View.VISIBLE

                                    }
                                } else {
                                    recycler.visibility = View.GONE
                                    binding.llNoInternetConnection.visibility = View.VISIBLE
                                }
                            }

                            override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                                recycler.visibility = View.GONE
                                binding.llNoInternetConnection.visibility = View.VISIBLE
                            }

                        })
                }
            }
            false
        }

        etFindText.addTextChangedListener(textWatcher)

        ivClear.setOnClickListener {
            etFindText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            tracks.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun setButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}