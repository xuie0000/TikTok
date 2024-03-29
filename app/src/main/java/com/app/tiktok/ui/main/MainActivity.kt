package com.app.tiktok.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.app.tiktok.R
import com.app.tiktok.base.BaseActivity
import com.app.tiktok.databinding.ActivityMainBinding
import com.app.tiktok.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavController.OnDestinationChangedListener {

  private lateinit var binding: ActivityMainBinding
  private val homeViewModel by viewModels<MainViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val navController = findNavController(R.id.nav_host_fragment)
    binding.navView.setupWithNavController(navController)

    navController.addOnDestinationChangedListener(this)

    binding.imageViewAddIcon.setOnClickListener { startSearchActivity() }
  }

  override fun onDestinationChanged(
    controller: NavController,
    destination: NavDestination,
    arguments: Bundle?
  ) {
    when (destination.id) {
      R.id.navigation_home -> {
        changeStatusBarColor(R.color.colorBlack)
        val colorDark = ContextCompat.getColorStateList(
          this,
          R.color.bottom_tab_selector_item_dark
        )

        val colorBlack = ContextCompat.getColorStateList(
          this,
          R.color.colorBlack
        )

        binding.navView.backgroundTintList = colorBlack
        binding.navView.itemTextColor = colorDark
        binding.navView.itemIconTintList = colorDark
        binding.imageViewAddIcon.setImageResource(R.drawable.ic_add_icon_light)
      }
      else -> {
        changeStatusBarColor(R.color.colorWhite)
        val colorDark = ContextCompat.getColorStateList(
          this,
          R.color.bottom_tab_selector_item_light
        )

        val colorWhite = ContextCompat.getColorStateList(
          this,
          R.color.colorWhite
        )

        binding.navView.backgroundTintList = colorWhite
        binding.navView.itemTextColor = colorDark
        binding.navView.itemIconTintList = colorDark
        binding.imageViewAddIcon.setImageResource(R.drawable.ic_add_icon_dark)
      }
    }
  }

  private fun startSearchActivity() {
    val options = ActivityOptions.makeSceneTransitionAnimation(this)
    ActivityCompat.startActivity(this, Intent(this, SearchActivity::class.java), options.toBundle())
  }
}