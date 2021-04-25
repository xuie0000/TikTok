package com.app.tiktok.utils

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.app.tiktok.model.TikTok

fun AppCompatTextView.setTextOrHide(value: String? = null) {
  if (!value.isNullOrBlank()) {
    text = value
    isVisible = true
  } else {
    isVisible = false
  }
}

fun Long.formatNumberAsReadableFormat(): String {
  return Utility.formatNumberAsNumberFormat(this)
}

fun TikTok.remoteUrl(): String {
  return "http://120.79.19.40:81/${this.storyUrl}"
}