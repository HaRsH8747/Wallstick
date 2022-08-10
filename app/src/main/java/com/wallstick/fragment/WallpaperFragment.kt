package com.wallstick.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wallstick.R
import com.wallstick.adapters.ViewPagerAdapter
import com.wallstick.databinding.FragmentWallpaperBinding

class WallpaperFragment : Fragment() {

    var tabTitle = arrayOf("Latest","Trending","Favourite","Search")
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var wallpaperBinding: FragmentWallpaperBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        wallpaperBinding = FragmentWallpaperBinding.inflate(layoutInflater)
        wallpaperBinding.viewpager2.adapter = ViewPagerAdapter(requireActivity().supportFragmentManager,requireActivity().lifecycle,wallpaperBinding.tvTitle)
        wallpaperBinding.searchView.setOnClickListener{
            if (wallpaperBinding.etSearch.visibility== View.VISIBLE){
                wallpaperBinding.etSearch.text.clear()
                wallpaperBinding.searchView.setImageResource(R.drawable.search)
                wallpaperBinding.tvTitle.visibility= View.VISIBLE
                wallpaperBinding.etSearch.visibility= View.GONE
                hideSoftKeyboard()
            }else{
                wallpaperBinding.tvTitle.visibility= View.GONE
                wallpaperBinding.etSearch.visibility= View.VISIBLE
                wallpaperBinding.viewpager2.currentItem = 3
                wallpaperBinding.etSearch.requestFocus()
                showSoftKeyboard()
                wallpaperBinding.searchView.setImageResource(R.drawable.close)
            }
        }
        when(arguments?.getInt("index")){
            0 -> wallpaperBinding.viewpager2.currentItem = 0
            1 -> wallpaperBinding.viewpager2.currentItem = 1
            2 -> wallpaperBinding.viewpager2.currentItem = 2
            3 -> wallpaperBinding.viewpager2.currentItem = 3
        }
        wallpaperBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.text){
                    tabTitle[0] -> wallpaperBinding.tvTitle.text = resources.getString(R.string.lw)
                    tabTitle[1] -> wallpaperBinding.tvTitle.text = resources.getString(R.string.tw)
                    tabTitle[2] -> wallpaperBinding.tvTitle.text = resources.getString(R.string.fw)
                    tabTitle[3] -> wallpaperBinding.tvTitle.text = resources.getString(R.string.se)
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

    fun hideSoftKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    fun showSoftKeyboard(){
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(wallpaperBinding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }
}