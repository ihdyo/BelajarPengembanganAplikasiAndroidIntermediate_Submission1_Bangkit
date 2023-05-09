package com.ihdyo.postit.utils.ext

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

fun View.gone() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun ImageView.setImageUrl(url: String, isCenterCrop: Boolean) {
    Glide.with(context)
        .load(url)
        .centerCrop()
        .into(this)
}