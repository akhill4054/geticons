package com.greedygames.geticons.utils

import android.content.Context
import android.widget.Toast

// Toast message extensions
fun Context.showShortToast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_SHORT
    ).show()
}