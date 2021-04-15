package com.app.tiktok.utils

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible

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