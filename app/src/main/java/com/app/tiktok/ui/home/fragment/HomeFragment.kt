package com.app.tiktok.ui.home.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.app.tiktok.R
import com.app.tiktok.base.BaseFragment
import com.app.tiktok.ui.home.adapter.HomePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = HomePagerAdapter(this)
        pager.offscreenPageLimit = 3

        val tabTitles = listOf("推荐", "关注", "同城")
        TabLayoutMediator(tab, pager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        initAction()
    }

    private fun initAction() {
        iv_online.setOnClickListener { Toast.makeText(requireContext(), "直播", Toast.LENGTH_SHORT).show() }
        iv_search.setOnClickListener { Toast.makeText(requireContext(), "搜索", Toast.LENGTH_SHORT).show() }
    }
}