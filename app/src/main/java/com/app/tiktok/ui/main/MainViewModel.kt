package com.app.tiktok.ui.main

import android.util.Log
import androidx.annotation.MainThread
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.app.tiktok.model.ResultData
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.model.TikTok
import com.app.tiktok.repository.DataRepository
import com.app.tiktok.repository.TikTokRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class MainViewModel @ViewModelInject constructor(
    private val dataRepository: DataRepository,
    private val tikTokRepository: TikTokRepository
) : ViewModel() {

    private val fetchingIndex: MutableStateFlow<Int> = MutableStateFlow(-1)
    val listLiveData: LiveData<ResultData<List<TikTok>?>>

    init {
        Log.d(TAG, "init")

        listLiveData = fetchingIndex.asLiveData().switchMap {
            flow {
                Log.d(TAG, "loading page $it")
                emit(ResultData.Loading())
                tikTokRepository.getTikTok(it)?.let {
                    fetchingIndex.value = it.number
                    Log.d(TAG, "result - page:${it.number}, first:${it.first}, last:${it.last}")
                    if (it.first) {
                        emit(ResultData.Refresh(it.content))
                        return@flow
                    }
                    emit(ResultData.Success(it.content))
                } ?: emit(ResultData.Failed("request failed"))
            }.asLiveData()
        }
    }

    @MainThread
    fun fetchList() = fetchingIndex.value++

    // the origin data list method
    fun getDataList(): LiveData<ResultData<ArrayList<StoriesDataModel>?>> {
        return flow {
            emit(ResultData.Loading())
            emit(ResultData.Success(dataRepository.getStoriesData()))
        }.asLiveData(Dispatchers.IO)
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}