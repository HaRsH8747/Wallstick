package com.wallstick.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.wallstick.R
import com.wallstick.databinding.FragmentFavouriteBinding

class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(layoutInflater,container,false)
        binding.searchView.setOnClickListener{
            if (binding.etSearch.visibility== View.VISIBLE){
                binding.etSearch.text.clear()
                binding.searchView.setImageResource(R.drawable.search)
                binding.tvTitle.visibility= View.VISIBLE
                binding.etSearch.visibility= View.GONE
            }else{
                binding.tvTitle.visibility= View.GONE
                binding.etSearch.visibility= View.VISIBLE
                binding.searchView.setImageResource(R.drawable.close)

            }
        }

        binding.backBtn.setOnClickListener { v -> Navigation.findNavController(v).popBackStack()}


        return binding.root
    }

}