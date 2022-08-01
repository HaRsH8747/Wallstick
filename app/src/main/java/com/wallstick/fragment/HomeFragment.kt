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

        binding.lsBtn.setOnClickListener { v -> findNavController(v).navigate(R.id.action_homeFragment2_to_latestFragment) }
        binding.twBtn.setOnClickListener { v -> findNavController(v).navigate(R.id.action_homeFragment2_to_trendingFragment) }
        binding.favBtn.setOnClickListener { v -> findNavController(v).navigate(R.id.action_homeFragment2_to_favouriteFragment) }

        return binding.root
    }
}