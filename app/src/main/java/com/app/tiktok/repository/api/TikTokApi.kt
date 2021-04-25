package com.app.tiktok.repository.api

import com.app.tiktok.model.ResultTiktok
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * http://120.79.19.40:8080/tiktok/list?size=2&page=0
 */
interface TikTokApi {

  @GET("tiktok/list")
  suspend fun getTikTok(
    @Query("page") page: Int,
    @Query("size") size: Int
  ): Response<ResultTiktok>
}