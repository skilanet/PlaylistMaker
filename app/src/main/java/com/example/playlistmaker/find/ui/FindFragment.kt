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
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFindBinding
import com.example.playlistmaker.find.domain.models.Song
import com.example.playlistmaker.find.domain.repository.DebounceInteractor
import com.example.playlistmaker.find.presentation.view_model.FindViewModel
import com.example.playlistmaker.find.ui.states.HistoryState
import com.example.playlistmaker.find.ui.states.TracksState
import com.example.playlistmaker.media_player.ui.MediaPlayerFragment
import com.example.playlistmaker.util.FragmentBinding
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FindFragment : FragmentBinding<FragmentFindBinding>() {

    private val adapter = SongsAdapter()
    private val historyAdapter = SongsAdapter()
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
    private val debounceInteractor: DebounceInteractor by inject()
    private val viewModel by viewModel<FindViewModel>()
    private val etFindText: EditText by lazy {
        binding.etFindText
    }

    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFindBinding = FragmentFindBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeSearchState().observe(viewLifecycleOwner) {
            renderState(it)
        }
        viewModel.observeHistoryState().observe(viewLifecycleOwner) {
            renderHistoryState(it)
        }

        val ivClear = binding.ivClear

        // adapter initialize
        rvFindShowTrack.adapter = adapter
        adapter.onItemClick = { onItemClick(it) }
        rvFindShowTrack.layoutManager = LinearLayoutManager(requireContext())

        binding.rvHistoryOfSearch.adapter = historyAdapter
        historyAdapter.onItemClick = { onItemClick(it) }
        binding.rvHistoryOfSearch.layoutManager = LinearLayoutManager(requireContext())

        etFindText.doOnTextChanged { text, _, _, _ ->
            ivClear.visibility = setButtonVisibility(text)
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

        ivClear.setOnClickListener {
            etFindText.setText(getString(R.string.empty_string))
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                0
            )
            val size = adapter.itemCount
            adapter.tracks.clear()
            adapter.notifyItemRangeChanged(0, size)
            hideAll()
            llHistoryOfSearch.visibility = View.VISIBLE
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

    private fun setButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
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
        llHistoryOfSearch.visibility = if (etFindText.text.isEmpty()) View.VISIBLE else View.GONE
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
            if (historyAdapter.tracks.size > 10) {
                historyAdapter.tracks.removeAt(9)
            }
            viewModel.updateHistoryState(historyAdapter.tracks)
            historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
            findNavController().navigate(
                R.id.action_findFragment_to_mediaPlayerFragment,
                MediaPlayerFragment.createArgs(createJsonFromSong(song))
            )
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