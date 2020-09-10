package com.app.tiktok.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.tiktok.ui.home.fragment.CityFragment
import com.app.tiktok.ui.home.fragment.FollowFragment
import com.app.tiktok.ui.home.fragment.RecommendFragment

/**
 * @author Jie Xu
 * @date 2020/9/10
 */
class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CityFragment()
            1 -> FollowFragment()
            2 -> RecommendFragment()
            else -> throw IllegalArgumentException("argument exception")
        }
    }
}