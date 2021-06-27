package com.greedygames.geticons.utils

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.greedygames.geticons.ERROR_BAD_RESPONSE
import com.greedygames.geticons.ERROR_NO_INTERNET
import com.greedygames.geticons.R

class SnackbarHelper {
    companion object {
        fun showSnackbar(root: View, message: String, anchorView: View? = null) {
            showSnackbarImpl(root, message, anchorView)
        }

        fun showError(root: View, code: Int, anchorView: View? = null) {
            val context = root.context
            val message = when (code) {
                ERROR_NO_INTERNET -> {
                    context.getString(R.string.warn_no_internet)
                }
                ERROR_BAD_RESPONSE -> {
                    context.getString(R.string.err_bad_response)
                }
                else -> {
                    context.getString(R.string.err_typical)
                }
            }
            showSnackbarImpl(root, message, anchorView, true)
        }

        private fun showSnackbarImpl(
            root: View,
            message: String,
            anchorView: View?,
            isError: Boolean = false
        ) {
            val sb = Snackbar.make(
                root,
                message,
                Snackbar.LENGTH_SHORT
            )
            if (anchorView != null) {
                sb.anchorView = anchorView
            }
            if (isError) {
                sb.setTextColor(Color.WHITE)
                sb.setBackgroundTint(ContextCompat.getColor(root.context, R.color.error))
            }
            sb.show()
        }
    }

    interface SnackbarListener {
        fun onShowSnackbar(message: String, anchorView: View? = null)
        fun onError(code: Int, anchorView: View? = null)
    }
}