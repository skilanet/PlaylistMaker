package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.models.EmailData
import com.example.playlistmaker.sharing.domain.models.ShareAppData
import com.example.playlistmaker.sharing.domain.models.TermsData
import com.example.playlistmaker.sharing.domain.repository.SharingRepository

class SharingRepositoryImpl(private val context: Context) : SharingRepository {
    override fun getShareAppData(): ShareAppData =
        ShareAppData(context.getString(R.string.settings_share_message))

    override fun getTermsData(): TermsData =
        TermsData(context.getString(R.string.settings_user_agreement_url))

    override fun getEmailData(): EmailData = EmailData(
        email = context.getString(R.string.setting_email_address),
        subject = context.getString(R.string.settings_email_title),
        text = context.getString(R.string.settings_email_text)
    )
}