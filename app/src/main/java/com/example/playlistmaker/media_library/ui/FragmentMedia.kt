package com.example.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.util.FragmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class FragmentMedia : FragmentBinding<FragmentMediaBinding>() {

    override fun createBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaBinding = FragmentMediaBinding.inflate(layoutInflater, container, false)

    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vpMedia.adapter = Adapter(childFragmentManager, lifecycle)
        tabLayoutMediator = TabLayoutMediator(binding.tlMedia, binding.vpMedia) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.favorite_tracks)
                }

                1 -> {
                    tab.text = getString(R.string.playlists)
                }
            }
        }
        tabLayoutMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }
}