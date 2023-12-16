package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.playlistmaker.databinding.ActivityFindBinding

class FindActivity : AppCompatActivity() {


    private var gettedString: String = DEFAULT
    private lateinit var binding: ActivityFindBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFindBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.findToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.ivClear.setOnClickListener {
            binding.etFindText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

        binding.etFindText.doOnTextChanged { text, start, before, count ->
            gettedString = text.toString()
            binding.ivClear.visibility = setButtonVisibility(text)
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
        const val KEY = "ADD KEY"
        const val DEFAULT = "DEFAULT"
    }
}