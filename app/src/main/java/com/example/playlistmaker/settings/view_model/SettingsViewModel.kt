package com.example.playlistmaker.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.repository.ThemeSharedPreferenceInteractor
import com.example.playlistmaker.sharing.domain.models.EmailData
import com.example.playlistmaker.sharing.domain.models.ShareAppData
import com.example.playlistmaker.sharing.domain.models.TermsData
import com.example.playlistmaker.sharing.domain.repository.SharingRepository
import com.example.playlistmaker.utils.Creator

class SettingsViewModel(
    sharingRepository: SharingRepository,
    private val sharedPreferenceInteractor: ThemeSharedPreferenceInteractor
) : ViewModel() {

    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[APPLICATION_KEY]!!.applicationContext
                SettingsViewModel(
                    Creator.provideSharingRepository(context),
                    Creator.provideThemeSharedPreference(context)
                )
            }
        }
    }

    private val themeState = MutableLiveData(sharedPreferenceInteractor.getThemeFromSharedPreference())
    fun updateThemeState(isDarkTheme: Boolean){
        sharedPreferenceInteractor.setThemeToSharedPreference(isDarkTheme)
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