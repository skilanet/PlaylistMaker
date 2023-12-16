package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val findButton = findViewById<Button>(R.id.btn_find)
        val mediaButton = findViewById<Button>(R.id.btn_media_library)
        val settingsButton = findViewById<Button>(R.id.btn_settings)

        val findClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val findIntent = Intent(this@MainActivity, FindActivity::class.java)
                startActivity(findIntent)
            }
        }

        findButton.setOnClickListener(findClickListener)
        mediaButton.setOnClickListener {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}