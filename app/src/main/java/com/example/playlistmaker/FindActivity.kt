package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class FindActivity : AppCompatActivity() {


    private var gettedString: String = DEFAULT

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        val inputText = findViewById<EditText>(R.id.find_input_text)
        val clearText = findViewById<ImageView>(R.id.clear_button)
        val backButton = findViewById<Toolbar>(R.id.find_toolbar)

        backButton.setNavigationOnClickListener {
            finish()
        }

        clearText.setOnClickListener {
            inputText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                gettedString = s.toString()
                clearText.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
//                TODO("Not yet implemented")
            }

        }
        inputText.addTextChangedListener(textWatcher)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
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