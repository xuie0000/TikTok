package com.app.tiktok.ui.story

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.app.tiktok.app.MyApp
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.utils.logError
import com.app.tiktok.widget.viewpagerlayoutmanager.OnViewPagerListener
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.common.net.HttpHeaders.USER_AGENT

/**
 * @author Jie Xu
 * @date 2020/9/14
 */
class StoryViewAdapter(
    private val clickCallback: ((view: View, position: Int, item: StoriesDataModel) -> Unit)?
) : ListAdapter<StoriesDataModel, StoryViewHolder>(storyDiffCallback) {

    private var simplePlayer: SimpleExoPlayer? = null
    private var cacheDataSourceFactory: DataSource.Factory? = null
    private var upstreamFactory: DataSource.Factory? = null
    private val simpleCache = MyApp.simpleCache
    private var storyUrl: String? = null

    init {
        simplePlayer = getPlayer()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder.create(parent, clickCallback, null)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position), simplePlayer)
    }


    val onViewPagerListener: OnViewPagerListener = object : OnViewPagerListener {
        override fun onInitComplete() {
            if (itemCount > 0) {
                val storyUrl = getItem(0).storyUrl
                this@StoryViewAdapter.storyUrl = storyUrl
                prepareMedia(storyUrl)
                restartVideo()
            }
        }

        override fun onPageRelease(isNext: Boolean, position: Int) {
        }

        override fun onPageSelected(position: Int, isBottom: Boolean) {
            val storyUrl = getItem(position).storyUrl

            this@StoryViewAdapter.storyUrl = storyUrl
            prepareMedia(storyUrl)
            restartVideo()

            // TODO 无法获取ITEM对像，playerViewStory.player = simplePlayer动作无法达成，先放弃这方法了
            // 这里适用有缩略图的情况，全局用一个播放器的方法
            // 参考于https://github.com/18380438200/Tiktok
            // 这里不喜欢这种方式的原因是，只用了一个播放器！！！不便于速度吧。。
        }
    }


    private fun getPlayer(): SimpleExoPlayer? {
        if (simplePlayer == null) {
            prepareVideoPlayer()
        }
        return simplePlayer
    }

    private fun prepareVideoPlayer() {
        simplePlayer = SimpleExoPlayer.Builder(MyApp.context).build()
        upstreamFactory = DefaultDataSourceFactory(MyApp.context, USER_AGENT)
        cacheDataSourceFactory = CacheDataSource.Factory().apply {
            setCache(simpleCache)
            setUpstreamDataSourceFactory(upstreamFactory)
            setFlags(
                CacheDataSource.FLAG_BLOCK_ON_CACHE or
                        CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
            )
        }
    }

    private val playerCallback: Player.EventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            logError("onPlayerStateChanged playbackState: $playbackState")
        }

        override fun onPlayerError(error: com.google.android.exoplayer2.ExoPlaybackException) {
            super.onPlayerError(error)
        }
    }

    private fun prepareMedia(linkUrl: String) {
        logError("prepareMedia linkUrl: $linkUrl")

        val uri = Uri.parse(linkUrl)

        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory!!).createMediaSource(
            MediaItem.Builder().setUri(uri).build()
        )

        simplePlayer?.setMediaSource(mediaSource, true)
        // FIXME 最新版本这里不能添加`.prepare`，否则出现不播放
//        simplePlayer?.prepare()
        simplePlayer?.repeatMode = Player.REPEAT_MODE_ONE
        simplePlayer?.playWhenReady = true
        simplePlayer?.addListener(playerCallback)
    }


    fun restartVideo() {
        if (simplePlayer == null) {
            storyUrl?.let {
                prepareMedia(it)
            }
        } else {
            simplePlayer?.seekToDefaultPosition()
            simplePlayer?.playWhenReady = true
        }
    }

    fun pauseVideo() {
        simplePlayer?.playWhenReady = false
    }

    private fun releasePlayer() {
        simplePlayer?.stop(true)
        simplePlayer?.release()
    }

    companion object {
        val storyDiffCallback = object : DiffUtil.ItemCallback<StoriesDataModel>() {
            override fun areItemsTheSame(oldItem: StoriesDataModel, newItem: StoriesDataModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoriesDataModel, newItem: StoriesDataModel): Boolean {
                return oldItem == newItem
            }

        }
    }

}
