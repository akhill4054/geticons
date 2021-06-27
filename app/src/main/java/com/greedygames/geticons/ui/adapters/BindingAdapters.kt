package com.greedygames.geticons.ui.adapters

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.util.Linkify
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.greedygames.geticons.NOT_AVAILABLE
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

@BindingAdapter("hyper_link_url", "hyper_link_text")
fun hyperLink(textView: TextView, url: String?, text: String?) {
    if (url != null && text != null) {
        val ss = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }

            override fun onClick(p0: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                ContextCompat.startActivity(textView.context, browserIntent, null)
            }
        }
        ss.setSpan(clickableSpan, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = ss
        textView.isClickable = true
        textView.movementMethod = LinkMovementMethod.getInstance()
    } else {
        if (text != null) {
            textView.text = text
        } else {
            textView.text = NOT_AVAILABLE
        }
        textView.setTextColor(
            ContextCompat.getColor(
                textView.context,
                R.color.text_secondary
            )
        )
    }
}