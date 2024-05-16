package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.App
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import com.example.playlistmaker.util.FragmentBinding
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment : FragmentBinding<FragmentSettingsBinding>() {

    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var scSwitchLightNightMode: SwitchMaterial

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scSwitchLightNightMode = binding.scSwitchLightNightMode

        binding.scSwitchLightNightMode.isChecked = viewModel.observeThemeState().value!!

        binding.ivShareApp.setOnClickListener {
            viewModel.observeShareAppState().observe(viewLifecycleOwner) {
                shareApp(it.url)
            }
        }

        binding.ivWriteToSupport.setOnClickListener {
            viewModel.observeWriteToSupportState().observe(viewLifecycleOwner) {
                writeToSupport(it.email, it.subject, it.text)
            }
        }

        binding.ivUserAgreement.setOnClickListener {
            viewModel.observeTermsState().observe(viewLifecycleOwner) {
                goToTerms(it.url)
            }
        }

        scSwitchLightNightMode.setOnCheckedChangeListener { _, isDarkTheme ->
            switchTheme(isDarkTheme)
        }
    }

    private fun switchTheme(isDarkTheme: Boolean) {
        (requireActivity().application as App).switchTheme(isDarkTheme)
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