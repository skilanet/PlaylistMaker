package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var scSwitchLightNightMode: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        scSwitchLightNightMode = binding.scSwitchLightNightMode

        viewModel = ViewModelProvider(
            this, SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        binding.scSwitchLightNightMode.isChecked = viewModel.observeThemeState().value!!

        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.ivShareApp.setOnClickListener {
            viewModel.observeShareAppState().observe(this) {
                shareApp(it.url)
            }
        }

        binding.ivWriteToSupport.setOnClickListener {
            viewModel.observeWriteToSupportState().observe(this) {
                writeToSupport(it.email, it.subject, it.text)
            }
        }

        binding.ivUserAgreement.setOnClickListener {
            viewModel.observeTermsState().observe(this) {
                goToTerms(it.url)
            }
        }

        scSwitchLightNightMode.setOnCheckedChangeListener { _, isDarkTheme ->
            switchTheme(isDarkTheme)
        }
    }

    private fun switchTheme(isDarkTheme: Boolean) {
        (applicationContext as App).switchTheme(isDarkTheme)
        viewModel.updateThemeState(isDarkTheme)
    }

    private fun shareApp(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, url)
        intent.type = "text/plain"
        startActivity(intent)
    }

    private fun writeToSupport(
        email: String, title: String, text: String
    ) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, title)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(intent)
    }

    private fun goToTerms(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}