package com.app.tiktok.ui.home.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.app.tiktok.R
import com.app.tiktok.model.ResultData
import com.app.tiktok.model.TikTok
import com.app.tiktok.ui.home.adapter.StoriesPagerAdapter
import com.app.tiktok.ui.main.MainViewModel
import com.app.tiktok.utils.Constants
import com.app.tiktok.work.PreCachingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recommend.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class RecommendFragment : Fragment(R.layout.fragment_recommend) {
  private val model by activityViewModels<MainViewModel>()

  private lateinit var storiesPagerAdapter: StoriesPagerAdapter

  @ExperimentalCoroutinesApi
  @InternalCoroutinesApi
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Timber.d("onCreated.")

    storiesPagerAdapter = StoriesPagerAdapter(this, mutableListOf()).apply {
      stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    view_pager_stories.adapter = storiesPagerAdapter
    view_pager_stories.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        Timber.d("selected $position")
        // 距离未尾4个时继续加载
        if (storiesPagerAdapter.dataList.size - position < 4) {
          Timber.d("need append, position at $position")
          model.fetchList()
        }
      }
    })

    lifecycleScope.launchWhenCreated {
      model.listFlow.collect {
        when (it) {
          is ResultData.Loading -> {
            // show loading
          }
          is ResultData.Success -> {
            it.data?.let { list ->
              storiesPagerAdapter.dataList.addAll(list)
//              storiesPagerAdapter.notifyDataSetChanged()
              storiesPagerAdapter.notifyItemChanged(storiesPagerAdapter.dataList.size - list.size)
              startPreCaching(list)
            }
          }
          is ResultData.Refresh -> {
            it.data?.let { list ->
              storiesPagerAdapter.dataList.clear()
              storiesPagerAdapter.dataList.addAll(list)
              storiesPagerAdapter.notifyItemChanged(0)
//              storiesPagerAdapter.notifyDataSetChanged()
              startPreCaching(list)
            }
          }
          is ResultData.Failed -> {
            // load failed!
            Timber.d("failed : ${it.message}")
          }
          else -> {

          }
        }
      }
    }

  }

  private fun startPreCaching(dataList: List<TikTok>) {
    val urlList = arrayOfNulls<String>(dataList.size)
    dataList.mapIndexed { index, storiesDataModel ->
      urlList[index] = "http://120.79.19.40:81/${storiesDataModel.storyUrl}"
    }
    val inputData = Data.Builder().putStringArray(Constants.KEY_STORIES_LIST_DATA, urlList).build()
    val preCachingWork = OneTimeWorkRequestBuilder<PreCachingService>().setInputData(inputData)
      .build()
    WorkManager.getInstance(requireContext())
      .enqueue(preCachingWork)
  }

}