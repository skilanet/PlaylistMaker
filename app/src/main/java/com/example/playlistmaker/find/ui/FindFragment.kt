package com.example.playlistmaker.find.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFindBinding
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.find.presentation.view_model.FindViewModel
import com.example.playlistmaker.find.ui.states.HistoryState
import com.example.playlistmaker.find.ui.states.TracksState
import com.example.playlistmaker.media_player.ui.MediaPlayerActivity
import com.example.playlistmaker.util.FragmentBinding
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FindFragment : FragmentBinding<FragmentFindBinding>() {

    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFindBinding = FragmentFindBinding.inflate(layoutInflater, container, false)

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val adapter = SongsAdapter(false)
    private val historyAdapter = SongsAdapter(true)
    private val viewModel by viewModel<FindViewModel>()
    private var isClickAllowed = true

    private var lastState = true

    private val pbLoading: ProgressBar by lazy {
        binding.pbLoading
    }
    private val llNothingNotFound: LinearLayout by lazy {
        binding.llNothingNotFound
    }
    private val llNoInternetConnection: LinearLayout by lazy {
        binding.llNoInternetConnection
    }
    private val llHistoryOfSearch: LinearLayout by lazy {
        binding.llHistoryOfSearch
    }
    private val rvFindShowTrack: RecyclerView by lazy {
        binding.rvFindShowTrack
    }
    private val etFindText: EditText by lazy {
        binding.etFindText
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFindShowTrack.adapter = adapter
        binding.rvFindShowTrack.layoutManager = LinearLayoutManager(requireActivity())

        binding.rvHistoryOfSearch.adapter = historyAdapter
        binding.rvHistoryOfSearch.layoutManager = LinearLayoutManager(requireActivity())

        adapter.onItemClick = { song, isHistory ->
            onItemClick(song, isHistory)
        }
        historyAdapter.onItemClick = { song, isHistory -> onItemClick(song, isHistory) }

        viewModel.observeSearchState().observe(viewLifecycleOwner) {
            renderState(it)

        }
        viewModel.observeHistoryState().observe(viewLifecycleOwner) {
            renderHistoryState(it)
        }

        val ivClear = binding.ivClear

        etFindText.doOnTextChanged { text, _, _, _ ->
            ivClear.isVisible = !text.isNullOrEmpty()
            if (etFindText.hasFocus() && text?.isEmpty() == true) {
                etFindText.isCursorVisible = etFindText.hasFocus()
                hideAll()
            } else binding.llHistoryOfSearch.visibility = View.GONE
            viewModel.searchDebounce(text.toString())
        }

        etFindText.setOnFocusChangeListener { _, hasFocus ->
            etFindText.isCursorVisible = hasFocus
            if (hasFocus && etFindText.text.isNotEmpty()) {
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
    }

    override fun setupListeners() {
        binding.ivClear.setOnClickListener {
            etFindText.setText(getString(R.string.empty_string))
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                0
            )
            val size = adapter.itemCount
            adapter.tracks.clear()
            adapter.notifyItemRangeChanged(0, size)
            hideAll()
            llHistoryOfSearch.isVisible = historyAdapter.tracks.isNotEmpty()
        }

        binding.btnUpdate.setOnClickListener {
            viewModel.sendRequest(etFindText.text.toString())
        }

        binding.btnClearSearchHistory.setOnClickListener {
            val size = historyAdapter.itemCount
            historyAdapter.tracks.clear()
            historyAdapter.notifyItemRangeChanged(0, size)
            viewModel.updateHistoryState(historyAdapter.tracks)
            hideAll()
        }
    }

    private fun showRecycle(tracks: List<Song>) {
        hideAll()
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyItemRangeChanged(0, adapter.itemCount)
        rvFindShowTrack.visibility = View.VISIBLE
    }

    private fun showHistory(history: ArrayList<Song>) {
        hideAll()
        historyAdapter.tracks = ArrayList()
        historyAdapter.tracks.addAll(history)
        historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
        llHistoryOfSearch.isVisible = etFindText.text.isEmpty()
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

    private fun onItemClick(song: Song, isHistory: Boolean) {
        if (clickDebounce()) {
            val contains = historyAdapter.tracks.contains(song)
            updateHistoryAdapter(contains, song)
            if (historyAdapter.tracks.size > 10) {
                historyAdapter.tracks.removeAt(9)
            }
            viewModel.updateHistoryState(historyAdapter.tracks)
            historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
            hideAll()
            lastState = isHistory
            val bundle = Bundle().apply {
                putString(MediaPlayerActivity.INTENT_PLAYLIST_KEY, createJsonFromSong(song))
            }
            findNavController().navigate(
                R.id.action_findFragment_to_mediaPlayerActivity, bundle
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (lastState) {
            hideAll()
            llHistoryOfSearch.isVisible = true && historyAdapter.tracks.isNotEmpty()
        } else {
            hideAll()
            rvFindShowTrack.isVisible = true
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}