package com.example.playlistmaker.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class FragmentBinding<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    val binding get() = _binding!!

    abstract fun createBinding(layoutInflater: LayoutInflater, container: ViewGroup?): T

    abstract fun setup()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createBinding(inflater, container)
        setup()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}