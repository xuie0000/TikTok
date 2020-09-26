package com.app.tiktok.ui.main.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.app.tiktok.model.ResultData
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.model.TikTok
import com.app.tiktok.repository.DataRepository
import com.app.tiktok.repository.api.TikTokApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow

class MainViewModel @ViewModelInject constructor(private val dataRepository: DataRepository) : ViewModel() {
    fun getDataList(): LiveData<ResultData<ArrayList<StoriesDataModel>?>> {
        return flow {
            emit(ResultData.Loading())
            emit(ResultData.Success(dataRepository.getStoriesData()))
        }.asLiveData(Dispatchers.IO)
    }

    private var page = -1
    private var first = true
    private var last = false

    fun appendList(): LiveData<MutableList<TikTok>?> {
        if (!last) {
            page++
        }
        return flow {
            Log.d(TAG, "11111")
            val data = TikTokApi.create().getTikTok(page).body()
            first = data?.first ?: false
            last = data?.last ?: true
            Log.d(TAG, "11111 $page $first  $last data:${data?.content}")
            if (!last) {
                page++
            }
            emit(data?.content)
        }.asLiveData()
    }


    companion object {
        const val TAG = "MainViewModel"
    }
}