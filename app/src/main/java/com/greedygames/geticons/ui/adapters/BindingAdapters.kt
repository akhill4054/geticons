package com.greedygames.geticons.ui.adapters

import android.os.Build
import android.text.Html
import android.text.util.Linkify
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
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
fun webUrl(textView: TextView, url: String?) {
    url?.let {
        textView.text = url
        Linkify.addLinks(textView, Linkify.WEB_URLS)
    }
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

@BindingAdapter("from_html")
fun fromHtml(textView: TextView, source: String?) {
    source?.let {
        textView.text = if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml(source, HtmlCompat.FROM_HTML_MODE_COMPACT);
        } else {
            Html.fromHtml(source)
        }
    }
}