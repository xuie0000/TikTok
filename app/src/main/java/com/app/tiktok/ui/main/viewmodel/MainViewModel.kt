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

    var data: LiveData<ResultData<List<TikTok>?>> = MutableLiveData()

    private var loading = false
    private var page = -1
    private var first = true
    private var last = false

    fun appendList() {
        if (last && loading) {
            return
        }
        loading = true
        page++
        Log.d(TAG, "loading page $page")
        data = flow {
            Log.d(TAG, "loading page 222  $page")
            emit(ResultData.Loading())
            val data = TikTokApi.create().getTikTok(page).body()
            loading = false
            data?.let {
                first = data.first
                last = data.last
                page = data.number
                Log.d(TAG, "result $page $first  $last data:${data.content}")
                if (first) {
                    emit(ResultData.Refresh(data.content))
                    return@flow
                }
                emit(ResultData.Success(data.content))
            }
        }.asLiveData()
    }


    companion object {
        const val TAG = "MainViewModel"
    }
}