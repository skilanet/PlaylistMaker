package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    private val sharedPreferenceInteractor = Creator.provideSharedPreferenceInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.scSwitchLightNightMode.isChecked = sharedPreferenceInteractor.getThiemeFromSharedPreference()

        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.ivShareApp.setOnClickListener {
            val message = getString(R.string.settings_share_message)
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"
            startActivity(intent)
        }

        binding.ivWriteToSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.setting_email_address)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_email_title))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_email_text))
            startActivity(intent)
        }

        binding.ivUserAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.settings_user_agreement_url))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        binding.scSwitchLightNightMode.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchThieme(checked)
            sharedPreferenceInteractor.setThiemeToSharedPreference(checked)
        }

    }
}