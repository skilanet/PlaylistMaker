package com.example.playlistmaker.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding

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
            }
        }
        val bottomNavView = binding.bottomNavView
        bottomNavView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_fragment_container_view)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}