/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.tiktok.ui.story

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.tiktok.R
import com.app.tiktok.databinding.LayoutStoryView2Binding
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.utils.formatNumberAsReadableFormat
import com.app.tiktok.utils.loadCenterCropImageFromUrl
import com.app.tiktok.utils.setTextOrHide
import com.app.tiktok.widget.viewpagerlayoutmanager.OnViewPagerListener
import com.google.android.exoplayer2.SimpleExoPlayer

class StoryViewHolder(
    private val binding: LayoutStoryView2Binding,
    private val clickCallback: ((view: View, position: Int, story: StoriesDataModel) -> Unit)?,
    private val longClickCallback: ((view: View, position: Int, article: StoriesDataModel) -> Boolean)?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: StoriesDataModel?, simplePlayer: SimpleExoPlayer?) {
        updateScore(story, simplePlayer)

        itemView.setOnClickListener {
            clickCallback?.invoke(it, absoluteAdapterPosition, story!!)
        }

        itemView.setOnLongClickListener {
            longClickCallback?.invoke(it, absoluteAdapterPosition, story!!) ?: false
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            clickCallback: ((view: View, position: Int, story: StoriesDataModel) -> Unit)?,
            longClickCallback: ((view: View, position: Int, story: StoriesDataModel) -> Boolean)?
        ): StoryViewHolder {
            return StoryViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_story_view2, parent, false
                ), clickCallback, longClickCallback
            )
        }
    }

    private fun updateScore(item: StoriesDataModel?, simplePlayer: SimpleExoPlayer?) {
        binding.run {
//            setVariable(BR.article, item)
//            executePendingBindings()
            textViewAccountHandle.setTextOrHide(value = item?.userName)
            textViewVideoDescription.setTextOrHide(value = item?.storyDescription)
            textViewMusicTitle.setTextOrHide(value = item?.musicCoverTitle)

            imageViewOptionCommentTitle.text = item?.commentsCount?.formatNumberAsReadableFormat()
            imageViewOptionLikeTitle.text = item?.likesCount?.formatNumberAsReadableFormat()

            imageViewProfilePic.loadCenterCropImageFromUrl(item?.userProfilePicUrl)

            textViewMusicTitle.isSelected = true

            playerViewStory.player = simplePlayer
        }
    }

}