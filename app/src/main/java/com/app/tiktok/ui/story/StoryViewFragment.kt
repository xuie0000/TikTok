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
import com.app.tiktok.utils.Constants
import com.app.tiktok.utils.formatNumberAsReadableFormat
import com.app.tiktok.utils.setTextOrHide
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

  private lateinit var storiesData: TikTok

  companion object {
    fun newInstance(tikTok: TikTok) = StoryViewFragment()
      .apply {
        arguments = Bundle().apply {
          putParcelable(Constants.KEY_STORY_DATA, tikTok)
        }
      }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    storiesData = arguments?.getParcelable(Constants.KEY_STORY_DATA)!!
  }

  private val viewModel by activityViewModels<MainViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    text_view_account_handle.setTextOrHide(value = storiesData.userName)
    text_view_video_description.setTextOrHide(value = storiesData.storyDescription)
    text_view_music_title.setTextOrHide(value = storiesData.musicCoverTitle)

    image_view_option_comment_title.text = storiesData.commentsCount.formatNumberAsReadableFormat()
    image_view_option_like_title.text = storiesData.likesCount.formatNumberAsReadableFormat()

//    image_view_profile_pic.loadCenterCropImageFromUrl(storiesDataModel.userProfilePicUrl)

    text_view_music_title.isSelected = true
    button_play_status.visibility = View.GONE

    prepareVideoPlayer()

    player_view_story.player = simplePlayer
    player_view_story.videoSurfaceView?.setOnClickListener {
      switchVideoStatus()
    }
    button_play_status.setOnClickListener {
      switchVideoStatus()
    }

    storyUrl = "http://120.79.19.40:81/${storiesData.storyUrl}"
    Timber.d("url : $storyUrl")
    prepareMedia(storyUrl)
  }

  private lateinit var storyUrl: String
  private lateinit var simplePlayer: SimpleExoPlayer
  private lateinit var cacheDataSourceFactory: DataSource.Factory


  private fun switchVideoStatus() {
    simplePlayer.run {
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
    Timber.d("onResume ${storiesData.storyId}")
    restartVideo()
    super.onResume()
  }

  override fun onPause() {
    Timber.d("onPause ${storiesData.storyId}")
    pauseVideo()
    super.onPause()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    releasePlayer()
  }

  private val playerCallback: Player.EventListener = object : Player.EventListener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
      Timber.d("onPlayerStateChanged: $playWhenReady-$playbackState")
    }

    override fun onPlayerError(error: com.google.android.exoplayer2.ExoPlaybackException) {
      super.onPlayerError(error)
      Timber.d("onPlayerError: $error")
    }
  }

  private fun prepareVideoPlayer() {
    simplePlayer = SimpleExoPlayer.Builder(requireContext()).build()

    val upstreamFactory = DefaultDataSourceFactory(requireContext(), HttpHeaders.USER_AGENT)
    cacheDataSourceFactory = CacheDataSource.Factory().apply {
      setCache(MyApp.simpleCache)
      setUpstreamDataSourceFactory(upstreamFactory)
      setFlags(
        CacheDataSource.FLAG_BLOCK_ON_CACHE or
          CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
      )
    }
  }

  private fun prepareMedia(linkUrl: String) {
    Timber.d("prepareMedia linkUrl: $linkUrl")

    val uri = Uri.parse(linkUrl)

    val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(
      MediaItem.Builder().setUri(uri).build()
    )

    simplePlayer.setMediaSource(mediaSource, true)
    simplePlayer.prepare()
    simplePlayer.repeatMode = Player.REPEAT_MODE_ONE
    simplePlayer.playWhenReady = true
    simplePlayer.addListener(playerCallback)

  }

  private fun setArtwork(drawable: Drawable, playerView: PlayerView) {
    playerView.useArtwork = true
    playerView.defaultArtwork = drawable
  }

  private fun playVideo() {
    simplePlayer.playWhenReady = true
  }

  private fun restartVideo() {
    button_play_status.visibility = View.GONE
    simplePlayer.seekToDefaultPosition()
    simplePlayer.playWhenReady = true
  }

  private fun pauseVideo() {
    simplePlayer.playWhenReady = false
  }

  private fun releasePlayer() {
    simplePlayer.stop()
    simplePlayer.release()
  }

}