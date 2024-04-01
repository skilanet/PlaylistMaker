package com.example.playlistmaker.sharing.domain.repository

import com.example.playlistmaker.sharing.domain.models.EmailData
import com.example.playlistmaker.sharing.domain.models.ShareAppData
import com.example.playlistmaker.sharing.domain.models.TermsData

interface SharingRepository {
    fun getShareAppData(): ShareAppData
    fun getTermsData(): TermsData
    fun getEmailData(): EmailData
}