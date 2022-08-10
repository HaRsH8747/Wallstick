package com.wallstick.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wallstick.R
import androidx.navigation.Navigation.findNavController
import com.wallstick.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)

        binding.lsBtn.setOnClickListener { v -> findNavController(v).navigate(HomeFragmentDirections.actionHomeFragment2ToWallpaperFragment(0)) }
        binding.twBtn.setOnClickListener { v -> findNavController(v).navigate(HomeFragmentDirections.actionHomeFragment2ToWallpaperFragment(1)) }
        binding.favBtn.setOnClickListener { v -> findNavController(v).navigate(HomeFragmentDirections.actionHomeFragment2ToWallpaperFragment(2)) }

        return binding.root
    }
}