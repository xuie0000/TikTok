package com.app.tiktok.repository.api

import com.app.tiktok.BuildConfig
import com.app.tiktok.model.ResultTiktok
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * http://120.79.19.40:8080/tiktok/list?size=2&page=0
 *
 * @author Jie Xu
 * @date 2020/9/23
 */
interface TikTokApi {

    @GET("tiktok/list")
    suspend fun getTikTok(
        @Query("page") page: Int,
        @Query("size") size: Int = 20
    ): Response<ResultTiktok>

    companion object {
        private const val BASE_URL = "http://120.79.19.40:8080/"

        fun create(): TikTokApi =
            create(BASE_URL.toHttpUrlOrNull()!!)

        fun create(httpUrl: HttpUrl): TikTokApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(httpUrl)
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .client(
                    OkHttpClient.Builder()
//                        .readTimeout(8000, TimeUnit.MILLISECONDS)
//                        .connectTimeout(8000, TimeUnit.MILLISECONDS)
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            if (BuildConfig.DEBUG) {
                                level = HttpLoggingInterceptor.Level.BODY
                            }
                        })
                        .build()
                )
                .build()
            return retrofit.create(TikTokApi::class.java)
        }
    }
}