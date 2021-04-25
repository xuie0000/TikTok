package com.app.tiktok.work

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.tiktok.app.MyApp
import com.app.tiktok.utils.Constants
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.common.net.HttpHeaders
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import timber.log.Timber

class PreCachingService(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

  override suspend fun doWork(): Result = coroutineScope {
    val upstreamFactory = DefaultDataSourceFactory(MyApp.context, HttpHeaders.USER_AGENT)
    val cacheDataSource = CacheDataSource.Factory().apply {
      setCache(MyApp.simpleCache)
      setUpstreamDataSourceFactory(upstreamFactory)
      setFlags(
        CacheDataSource.FLAG_BLOCK_ON_CACHE or
          CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
      )
    }.createDataSource()

    val dataList = inputData.getStringArray(Constants.KEY_STORIES_LIST_DATA)

    val jobs = dataList?.map { data ->
      async {
        val dataUri = Uri.parse(data)
        val dataSpec = DataSpec(dataUri, 0, 500 * 1024, null)

        preloadVideo(dataSpec, cacheDataSource) { requestLength: Long, bytesCached: Long, newBytesCached: Long ->
          val downloadPercentage = (bytesCached * 100.0 / requestLength)
          Timber.d("downloadPercentage: $downloadPercentage")
        }
      }
    }
    jobs?.joinAll()
    Result.success()
  }

  private fun preloadVideo(
    dataSpec: DataSpec,
    upstream: CacheDataSource,
    progressListener: CacheWriter.ProgressListener
  ) {
    Timber.d("preloadVideo")
    try {
      CacheWriter(upstream, dataSpec, true, null, progressListener).cache()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}