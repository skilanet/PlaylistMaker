package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val settingsToolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        settingsToolbar.setNavigationOnClickListener {
            val backToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(backToMainActivity)
        }
    }
}