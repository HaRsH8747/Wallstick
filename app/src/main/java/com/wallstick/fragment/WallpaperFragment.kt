package com.wallstick.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wallstick.R
import com.wallstick.adapters.ViewPagerAdapter
import com.wallstick.databinding.FragmentWallpaperBinding
import com.wallstick.utils.AppPref
import com.wallstick.utils.ConnectionLiveData

class WallpaperFragment : Fragment() {

    var tabTitle = arrayOf("Latest","Trending","Favourite")

    private lateinit var wallpaperBinding: FragmentWallpaperBinding
    private lateinit var snackbar: Snackbar
    private lateinit var connectionLiveData: ConnectionLiveData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        wallpaperBinding = FragmentWallpaperBinding.inflate(layoutInflater)
        wallpaperBinding.viewpager2.adapter = ViewPagerAdapter(requireActivity().supportFragmentManager,requireActivity().lifecycle,wallpaperBinding.tvTitle)
        addNetworkListener()
        wallpaperBinding.searchView.setOnClickListener{
            findNavController().navigate(R.id.action_wallpaperFragment_to_searchFragment)
//            if (wallpaperBinding.etSearch.visibility== View.VISIBLE){
//                wallpaperBinding.etSearch.text.clear()
//                wallpaperBinding.searchView.setImageResource(R.drawable.search)
//                wallpaperBinding.tvTitle.visibility= View.VISIBLE
//                wallpaperBinding.etSearch.visibility= View.GONE
//                hideSoftKeyboard()
//            }else{
////                FragmentActivity.getSupp
//                wallpaperBinding.tvTitle.visibility= View.GONE
//                wallpaperBinding.etSearch.visibility= View.VISIBLE
//                wallpaperBinding.viewpager2.currentItem = 3
//                wallpaperBinding.etSearch.requestFocus()
//                showSoftKeyboard()
//                wallpaperBinding.searchView.setImageResource(R.drawable.close)
//            }
        }
        when(arguments?.getInt("index")){
            0 -> wallpaperBinding.viewpager2.currentItem = 0
            1 -> wallpaperBinding.viewpager2.currentItem = 1
            2 -> wallpaperBinding.viewpager2.currentItem = 2
//            3 -> wallpaperBinding.viewpager2.currentItem = 3
        }
        wallpaperBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.text){
                    tabTitle[0] -> wallpaperBinding.tvTitle.text = resources.getString(R.string.lw)
                    tabTitle[1] -> wallpaperBinding.tvTitle.text = resources.getString(R.string.tw)
                    tabTitle[2] -> wallpaperBinding.tvTitle.text = resources.getString(R.string.fw)
//                    tabTitle[3] -> wallpaperBinding.tvTitle.text = resources.getString(R.string.se)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        wallpaperBinding.backBtn.setOnClickListener { v -> Navigation.findNavController(v).popBackStack() }
        TabLayoutMediator(wallpaperBinding.tabLayout,wallpaperBinding.viewpager2){
            tab,position -> tab.text = tabTitle[position]
        }.attach()

        return wallpaperBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snackbar = Snackbar.make(wallpaperBinding.root,"You are offline, Please turn on the internet to get full experience",Snackbar.LENGTH_INDEFINITE)
    }

    private fun addNetworkListener(){
        connectionLiveData= ConnectionLiveData(requireActivity().application)
        connectionLiveData.observe(viewLifecycleOwner) { isAvailable ->
            when (isAvailable) {
                true -> {
                    snackbar.dismiss()
                }
                false -> {
                    snackbar.show()
                }
            }
        }
    }
}