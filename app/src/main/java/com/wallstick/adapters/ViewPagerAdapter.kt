package com.wallstick.adapters

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wallstick.fragment.FavouriteFragment
import com.wallstick.fragment.LatestFragment
import com.wallstick.fragment.SearchFragment
import com.wallstick.fragment.TrendingFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, var tvTitle: TextView) : FragmentStateAdapter(fragmentManager,lifecycle){

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                LatestFragment()
            }
            1 -> {
                TrendingFragment()
            }
            2 -> {
                FavouriteFragment()
            }
//            3 -> {
//                SearchFragment()
//            }
            else -> {
                LatestFragment()
            }
        }
    }
}