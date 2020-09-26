package com.app.tiktok.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TikTok(
    val commentsCount: Long,
    val contentWarning: String,
    val likesCount: Long,
    val musicCoverImageLink: String,
    val musicCoverTitle: String,
    val storyDescription: String,
    val storyId: Int,
    val storyThumbUrl: String,
    val storyUrl: String,
    val userId: Int,
    val userName: String,
    val userProfilePicUrl: String
) : Parcelable