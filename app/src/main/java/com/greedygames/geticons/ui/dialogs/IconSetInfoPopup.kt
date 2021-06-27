package com.greedygames.geticons.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.databinding.PopupIconSetInfoBinding

class IconSetInfoPopup(
    context: Context,
    iconSet: IconSet
) {
    private val popupWindow = PopupWindow(context)
    private val binding = PopupIconSetInfoBinding.inflate(
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
        binding.iconSet = iconSet
    }

    fun show(anchorView: View) {
        popupWindow.showAsDropDown(anchorView)
    }
}