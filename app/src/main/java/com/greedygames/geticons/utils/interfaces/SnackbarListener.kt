package com.greedygames.geticons.utils.interfaces

import android.view.View

interface SnackbarListener {
    fun onShowSnackbar(message: String, anchorView: View? = null)
    fun onError(code: Int, anchorView: View? = null)
}