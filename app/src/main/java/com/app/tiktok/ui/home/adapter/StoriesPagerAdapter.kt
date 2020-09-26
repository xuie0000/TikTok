package com.app.tiktok.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.tiktok.model.TikTok
import com.app.tiktok.ui.story.StoryViewFragment

class StoriesPagerAdapter(fragment: Fragment, val dataList: MutableList<TikTok> = mutableListOf()) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        return StoryViewFragment.newInstance(dataList[position])
    }
}