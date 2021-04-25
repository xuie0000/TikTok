package com.app.tiktok.ui.main

import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.app.tiktok.model.ResultData
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.model.TikTok
import com.app.tiktok.repository.DataRepository
import com.app.tiktok.repository.TikTokRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val dataRepository: DataRepository,
  private val tikTokRepository: TikTokRepository
) : ViewModel() {

  private val fetchingIndex: MutableStateFlow<Int> = MutableStateFlow(0)
  private var isFinished = false

  @ExperimentalCoroutinesApi
  val listFlow: Flow<ResultData<List<TikTok>?>> = fetchingIndex.flatMapLatest {
    flow {
      Timber.d("index $it")
      if (isFinished) {
        emit(ResultData.Failed("fetch is over."))
        return@flow
      }
      emit(ResultData.Loading())
      tikTokRepository.getTikTok(it)?.let {
        fetchingIndex.value = it.number
        Timber.d("result ${it.size} - page:${it.number}, first:${it.first}, last:${it.last}")
        if (it.first) {
          emit(ResultData.Refresh(it.content))
          return@flow
        }
        if (it.last) {
          isFinished = true
        }
        emit(ResultData.Success(it.content))
      } ?: emit(ResultData.Failed("request failed."))
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

}