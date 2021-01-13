package com.app.tiktok.di

import com.app.tiktok.mock.Mock
import com.app.tiktok.repository.DataRepository
import com.app.tiktok.repository.TikTokRepository
import com.app.tiktok.repository.api.TikTokApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@InstallIn(ActivityRetainedComponent::class)
@Module
object RepositoryModule {
    @Provides
    fun providesDataRepository(mock: Mock): DataRepository {
        return DataRepository(mock)
    }

    @Provides
    fun providesApiRepository(api: TikTokApi): TikTokRepository {
        return TikTokRepository(api)
    }
}