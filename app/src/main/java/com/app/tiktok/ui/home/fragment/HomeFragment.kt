package com.app.tiktok.ui.home.fragment

import android.os.Bundle
import android.view.View
import com.app.tiktok.R
import com.app.tiktok.base.BaseFragment
import com.app.tiktok.ui.home.adapter.HomePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = HomePagerAdapter(this)

        val tabTitles = listOf("同城", "关注", "推荐")
        TabLayoutMediator(tab, pager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
        pager.currentItem = 2

    }
}