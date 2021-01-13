package com.app.tiktok.repository

import com.app.tiktok.model.ResultTiktok
import com.app.tiktok.repository.api.TikTokApi
import javax.inject.Inject

class TikTokRepository @Inject constructor(private val api: TikTokApi) {
    suspend fun getTikTok(page: Int, size: Int = 5): ResultTiktok? {
        return api.getTikTok(page, size).body()
    }
}