package com.greedygames.geticons.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.greedygames.geticons.data.models.License
import com.greedygames.geticons.databinding.PopupLicenseInfoBinding

class LicenseInfoPopup(
    context: Context,
    license: License
) {
    private val popupWindow = PopupWindow(context)
    private val binding = PopupLicenseInfoBinding.inflate(
        LayoutInflater.from(context),
        null,
        false
    )

    init {
        popupWindow.run {
            isFocusable = true
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            contentView = binding.root
            elevation = 3F
            setBackgroundDrawable(null)
        }
        binding.license = license
    }

    fun show(anchorView: View) {
        popupWindow.showAsDropDown(anchorView)
    }
}