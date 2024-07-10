package com.example.playlistmaker.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import com.example.playlistmaker.media_player.ui.MediaPlayerActivity

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostManager =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container_view) as NavHostFragment
        val navController = navHostManager.navController.apply {
            addOnDestinationChangedListener { _, destination, _ ->
                binding.bottomNavView.isVisible = destination.id != R.id.mediaPlayerActivity
                binding.bottomNavView.isVisible = destination.id != R.id.addPlaylistFragment
            }
        }
        val bottomNavView = binding.bottomNavView
        bottomNavView.setupWithNavController(navController)

        when (intent.extras?.getInt(MediaPlayerActivity.FRAGMENT_KEY)) {
            1 -> navController.navigate(R.id.addPlaylistFragment, bundleOf(with(MediaPlayerActivity){
                FRAGMENT_KEY to FRAGMENT_CODE
            }))
            else -> {}
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_fragment_container_view)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}