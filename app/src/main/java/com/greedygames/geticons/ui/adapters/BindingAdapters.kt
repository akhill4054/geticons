package com.greedygames.geticons.ui.adapters

import android.text.util.Linkify
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.greedygames.geticons.R

@BindingAdapter("isVisible")
fun isVisible(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("web_Url")
fun isVisible(textView: TextView, url: String) {
    textView.text = url
    Linkify.addLinks(textView, Linkify.WEB_URLS)
}

@BindingAdapter("preview_url")
fun previewUrl(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imageView)
            .load(url)
            .error(R.drawable.icon_preview_placeholder)
            .placeholder(R.drawable.icon_preview_placeholder)
            .into(imageView)
    } else {
        imageView.setImageResource(R.drawable.icon_preview_placeholder)
    }
}