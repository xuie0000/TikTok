package com.app.tiktok.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.app.tiktok.R
import com.app.tiktok.model.ResultData
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.ui.main.MainViewModel
import com.app.tiktok.ui.story.StoryViewAdapter
import com.app.tiktok.utils.Constants
import com.app.tiktok.widget.viewpagerlayoutmanager.ViewPagerLayoutManager
import com.app.tiktok.work.PreCachingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recommend_recycler_view.*

@AndroidEntryPoint
class RecommendRecyclerViewFragment : Fragment(R.layout.fragment_recommend_recycler_view) {
    private val homeViewModel by activityViewModels<MainViewModel>()

    private lateinit var storyViewPager: StoryViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storiesData = homeViewModel.getDataList()

        storiesData.observe(viewLifecycleOwner, { value ->
            when (value) {
                is ResultData.Loading -> {
                }
                is ResultData.Success -> {
                    if (!value.data.isNullOrEmpty()) {
                        val dataList = value.data
                        storyViewPager = StoryViewAdapter { view, position, item ->
                            Log.d("RecyclerViewFragment", "position:$position")
                        }
                        val viewPagerLayoutManager = ViewPagerLayoutManager(requireContext())
                        viewPagerLayoutManager.setOnViewPagerListener(storyViewPager.onViewPagerListener)
                        recycler_view.layoutManager = viewPagerLayoutManager
                        recycler_view.adapter = storyViewPager
                        (recycler_view.layoutManager as ViewPagerLayoutManager).findViewByPosition(2)

                        Log.d("RecyclerViewFragment", "size:${dataList.size}")
                        storyViewPager.submitList(dataList)

                        startPreCaching(dataList)
                    }
                }
                else -> {

                }
            }
        })
    }

    private fun startPreCaching(dataList: ArrayList<StoriesDataModel>) {
        val urlList = arrayOfNulls<String>(dataList.size)
        dataList.mapIndexed { index, storiesDataModel ->
            urlList[index] = storiesDataModel.storyUrl
        }
        val inputData = Data.Builder().putStringArray(Constants.KEY_STORIES_LIST_DATA, urlList).build()
        val preCachingWork = OneTimeWorkRequestBuilder<PreCachingService>().setInputData(inputData)
            .build()
        WorkManager.getInstance(requireContext())
            .enqueue(preCachingWork)
    }

    override fun onResume() {
        super.onResume()
        storyViewPager.restartVideo()
    }

    override fun onPause() {
        super.onPause()
        storyViewPager.pauseVideo()
    }


}