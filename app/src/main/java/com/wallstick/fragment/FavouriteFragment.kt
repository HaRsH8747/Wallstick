package com.wallstick.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.wallstick.R
import com.wallstick.adapters.PhotosAdapter
import com.wallstick.database.PhotoViewModel
import com.wallstick.databinding.FragmentFavouriteBinding

class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var mPhotoViewModel: PhotoViewModel
    private lateinit var adapter: PhotosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(layoutInflater,container,false)
        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)

        adapter = PhotosAdapter(requireContext(),mPhotoViewModel)
        binding.rvLatest.adapter = adapter
//        if (mPhotoViewModel.readFavourites.value!!.isEmpty()){
//            binding.tvEmpty.visibility = View.VISIBLE
//        }else{
//            binding.tvEmpty.visibility = View.GONE
//            adapter.setData(mPhotoViewModel.readFavourites.value!!)
//        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mPhotoViewModel.readFavourites.observe(this,Observer{
            if (it.isEmpty()){
                binding.rvLatest.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }else{
                binding.rvLatest.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
                adapter.setData(it, false)
            }
        })
    }

}