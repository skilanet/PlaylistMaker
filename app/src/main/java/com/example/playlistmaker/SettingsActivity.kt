package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val settingsToolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        val shareApp = findViewById<ImageView>(R.id.image_share_app)
        val writeToSupport = findViewById<ImageView>(R.id.image_write_to_support)
        val userAgreement = findViewById<ImageView>(R.id.image_user_agreement)

        shareApp.setOnClickListener {
            val message = getString(R.string.settings_share_message)
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"
            startActivity(intent)
        }

        writeToSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.setting_email_addres)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_email_title))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_email_text))
            startActivity(intent)
        }

        userAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.settings_user_agreement_url))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
    }
}