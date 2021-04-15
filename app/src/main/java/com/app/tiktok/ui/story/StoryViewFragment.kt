package com.app.tiktok.ui.story

import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.app.tiktok.R
import com.app.tiktok.app.MyApp
import com.app.tiktok.model.TikTok
import com.app.tiktok.ui.main.MainViewModel
import com.app.tiktok.utils.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.common.net.HttpHeaders
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_story_view.*
import timber.log.Timber

@AndroidEntryPoint
class StoryViewFragment : Fragment(R.layout.fragment_story_view) {
    private var storyUrl: String? = null
    private var storiesDataModel: TikTok? = null

    private var simplePlayer: SimpleExoPlayer? = null
    private var cacheDataSourceFactory: DataSource.Factory? = null
    private var upstreamFactory: DataSource.Factory? = null
    private val simpleCache = MyApp.simpleCache
    private var toPlayVideoPosition: Int = -1

    companion object {
        fun newInstance(tikTok: TikTok) = StoryViewFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.KEY_STORY_DATA, tikTok)
                }
            }
    }

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storiesDataModel = arguments?.getParcelable(Constants.KEY_STORY_DATA)
        setData()
    }

    private fun setData() {
        text_view_account_handle.setTextOrHide(value = storiesDataModel?.userName)
        text_view_video_description.setTextOrHide(value = storiesDataModel?.storyDescription)
        text_view_music_title.setTextOrHide(value = storiesDataModel?.musicCoverTitle)

        image_view_option_comment_title?.text = storiesDataModel?.commentsCount?.formatNumberAsReadableFormat()
        image_view_option_like_title?.text = storiesDataModel?.likesCount?.formatNumberAsReadableFormat()

        image_view_profile_pic?.loadCenterCropImageFromUrl(storiesDataModel?.userProfilePicUrl)

        text_view_music_title.isSelected = true
        button_play_status.visibility = View.GONE

        val simplePlayer = getPlayer()
        player_view_story.player = simplePlayer
        player_view_story?.videoSurfaceView?.setOnClickListener {
            switchVideoStatus(simplePlayer)
        }
        button_play_status?.setOnClickListener {
            switchVideoStatus(simplePlayer)
        }

        storyUrl = "http://120.79.19.40:81/${storiesDataModel?.storyUrl}"
        Timber.d("url : $storyUrl")
        storyUrl?.let { prepareMedia(it) }
    }

    private fun switchVideoStatus(simplePlayer: SimpleExoPlayer?) {
        simplePlayer?.run {
            if (isPlaying) {
                pauseVideo()
                button_play_status?.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(button_play_status, "scaleX", 2.0f, 1.0f).start()
                ObjectAnimator.ofFloat(button_play_status, "scaleY", 2.0f, 1.0f).start()
            } else {
                playVideo()
                button_play_status?.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        Timber.d("onResume ${storiesDataModel?.storyId}")
        restartVideo()
        super.onResume()
    }

    override fun onPause() {
        Timber.d("onPause ${storiesDataModel?.storyId}")
        pauseVideo()
        super.onPause()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private val playerCallback: Player.EventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            logError("onPlayerStateChanged playbackState: $playbackState")
        }

        override fun onPlayerError(error: com.google.android.exoplayer2.ExoPlaybackException) {
            super.onPlayerError(error)
        }
    }

    private fun prepareVideoPlayer() {
        simplePlayer = SimpleExoPlayer.Builder(requireContext()).build()
        upstreamFactory = DefaultDataSourceFactory(MyApp.context, HttpHeaders.USER_AGENT)
        cacheDataSourceFactory = CacheDataSource.Factory().apply {
            setCache(simpleCache)
            setUpstreamDataSourceFactory(upstreamFactory)
            setFlags(
                CacheDataSource.FLAG_BLOCK_ON_CACHE or
                        CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
            )
        }
    }

    private fun getPlayer(): SimpleExoPlayer? {
        if (simplePlayer == null) {
            prepareVideoPlayer()
        }
        return simplePlayer
    }

    private fun prepareMedia(linkUrl: String) {
        logError("prepareMedia linkUrl: $linkUrl")

        val uri = Uri.parse(linkUrl)

        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory!!).createMediaSource(
            MediaItem.Builder().setUri(uri).build()
        )

        simplePlayer?.setMediaSource(mediaSource, true)
        simplePlayer?.prepare()
        simplePlayer?.repeatMode = Player.REPEAT_MODE_ONE
//        simplePlayer?.playWhenReady = true
        simplePlayer?.addListener(playerCallback)

        toPlayVideoPosition = -1
    }

    private fun setArtwork(drawable: Drawable, playerView: PlayerView) {
        playerView.useArtwork = true
        playerView.defaultArtwork = drawable
    }

    private fun playVideo() {
        simplePlayer?.playWhenReady = true
    }

    private fun restartVideo() {
        button_play_status?.visibility = View.GONE
        if (simplePlayer == null) {
            storyUrl?.let { prepareMedia(it) }
            simplePlayer?.playWhenReady = true
        } else {
            simplePlayer?.seekToDefaultPosition()
            simplePlayer?.playWhenReady = true
        }
    }

    private fun pauseVideo() {
        simplePlayer?.playWhenReady = false
    }

    private fun releasePlayer() {
        simplePlayer?.stop(true)
        simplePlayer?.release()
    }

}