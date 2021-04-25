package com.app.tiktok.app

import android.app.Application
import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp : Application() {
  companion object {
    lateinit var simpleCache: SimpleCache
    lateinit var context: Context
  }

  override fun onCreate() {
    super.onCreate()
    context = this
    Timber.plant(Timber.DebugTree())

    val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(900 * 1024 * 1024)
    val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)

    simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
  }
}