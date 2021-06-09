package com.app.tiktok.ui.home.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.app.tiktok.R
import com.app.tiktok.base.BaseFragment
import com.app.tiktok.databinding.FragmentHomeBinding
import com.app.tiktok.ui.home.adapter.HomePagerAdapter
import com.app.tiktok.ui.search.SearchActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

  private val binding: FragmentHomeBinding by viewBinding()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.pager.adapter = HomePagerAdapter(this)
    binding.pager.offscreenPageLimit = 3

    val tabTitles = listOf("推荐", "关注", "同城")
    TabLayoutMediator(binding.tab, binding.pager) { tab, position ->
      tab.text = tabTitles[position]
    }.attach()

    initAction()
  }

  private fun initAction() {
    binding.ivOnline.setOnClickListener { Toast.makeText(requireContext(), "直播", Toast.LENGTH_SHORT).show() }
    binding.ivSearch.setOnClickListener { startSearchActivity() }
  }

  private fun startSearchActivity() {
    val activity = requireActivity()
    val options = ActivityOptions.makeSceneTransitionAnimation(activity)
    ActivityCompat.startActivity(activity, Intent(activity, SearchActivity::class.java), options.toBundle())
  }

}