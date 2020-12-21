package com.app.tiktok.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.app.tiktok.R
import com.app.tiktok.model.ResultData
import com.app.tiktok.model.TikTok
import com.app.tiktok.ui.home.adapter.StoriesPagerAdapter
import com.app.tiktok.ui.main.viewmodel.MainViewModel
import com.app.tiktok.utils.Constants
import com.app.tiktok.work.PreCachingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recommend.*

@AndroidEntryPoint
class RecommendFragment : Fragment(R.layout.fragment_recommend) {
    private val model by activityViewModels<MainViewModel>()

    private lateinit var storiesPagerAdapter: StoriesPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onCreated.")

        storiesPagerAdapter = StoriesPagerAdapter(this, mutableListOf()).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        view_pager_stories.adapter = storiesPagerAdapter
        view_pager_stories.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(TAG, "selected $position")
                // 距离未尾4个时继续加载
                if (storiesPagerAdapter.dataList.size - position < 4) {
                    Log.d(TAG, "need append, position at $position")
                    model.fetchList()
                }
            }
        })

        model.listLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is ResultData.Loading -> {

                }
                is ResultData.Success -> {
                    it.data?.let { list ->
                        storiesPagerAdapter.dataList.addAll(list)
                        storiesPagerAdapter.notifyDataSetChanged()
                        startPreCaching(list)
                    }
                }
                is ResultData.Refresh -> {
                    it.data?.let { list ->
                        storiesPagerAdapter.dataList.clear()
                        storiesPagerAdapter.dataList.addAll(list)
                        storiesPagerAdapter.notifyDataSetChanged()
                        startPreCaching(list)
                    }
                }
                else -> {

                }
            }
        })
        model.fetchList()
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

    companion object {
        const val TAG = "RecommendFragment"
    }
}