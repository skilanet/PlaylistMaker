package com.example.playlistmaker.media_library.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class Adapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(
    fragmentManager, lifecycle
) {

    private val chooseFragment = arrayOf(
        { FavoriteTracksFragment() },
        { PlaylistsFragment() }
    )

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = chooseFragment[position].invoke()
}