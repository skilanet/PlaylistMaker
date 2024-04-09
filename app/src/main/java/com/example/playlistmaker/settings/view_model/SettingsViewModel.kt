package com.example.playlistmaker.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.repository.ThemeSharedPreference
import com.example.playlistmaker.sharing.domain.models.EmailData
import com.example.playlistmaker.sharing.domain.models.ShareAppData
import com.example.playlistmaker.sharing.domain.models.TermsData
import com.example.playlistmaker.sharing.domain.repository.SharingRepository

class SettingsViewModel(
    sharingRepository: SharingRepository,
    private val themeSharedPreference: ThemeSharedPreference
) : ViewModel() {

    private val themeState = MutableLiveData(themeSharedPreference.getThemeFromSharedPreference())
    fun updateThemeState(isDarkTheme: Boolean){
        themeSharedPreference.setThemeToSharedPreference(isDarkTheme)
        themeState.postValue(isDarkTheme)
    }
    fun observeThemeState(): LiveData<Boolean> = themeState

    private val termsState = SingleLiveEvent<TermsData>()
    private val shareAppState = SingleLiveEvent<ShareAppData>()
    private val writeToSupportState = SingleLiveEvent<EmailData>()
    init {
        termsState.postValue(sharingRepository.getTermsData())
        shareAppState.postValue(sharingRepository.getShareAppData())
        writeToSupportState.postValue(sharingRepository.getEmailData())
    }
    fun observeTermsState(): LiveData<TermsData> = termsState
    fun observeShareAppState(): LiveData<ShareAppData> = shareAppState
    fun observeWriteToSupportState(): LiveData<EmailData> = writeToSupportState
}