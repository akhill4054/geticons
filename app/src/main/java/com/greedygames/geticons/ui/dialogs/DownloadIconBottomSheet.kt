package com.greedygames.geticons.ui.dialogs

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors
import com.greedygames.geticons.R
import com.greedygames.geticons.data.models.Icon
import com.greedygames.geticons.databinding.BottomSheetDownloadIconBinding
import com.greedygames.geticons.utils.IconDownloadHelper
import com.greedygames.geticons.utils.showShortToast

class DownloadIconBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    private var _binding: BottomSheetDownloadIconBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var downloadFormats: List<Icon.DownloadFormat>

    private val downloadHelper by lazy { IconDownloadHelper(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Passed args
        try {
            downloadFormats =
                requireArguments().getParcelableArrayList(ARG_DOWNLOAD_FORMATS)!!
        } catch (e: Exception) {
            throw RuntimeException(
                "List of ${Icon.DownloadFormat::class.simpleName}" +
                        "must be passed to ${DownloadIconBottomSheet::class.simpleName}"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDownloadIconBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // For rounded corners.
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet =
                    dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
                bottomSheet!!.setBackgroundResource(R.drawable.bg_bottom_sheet)
            }
        })
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Setup chip view for download format selection
        val chipBgColor = ColorStateList.valueOf(
            MaterialColors.getColor(
                requireContext(),
                R.attr.colorPrimary,
                ContextCompat.getColor(requireContext(), R.color.purple_200)
            )
        )
        for (format in downloadFormats) {
            val chip = Chip(requireContext())
            chip.chipBackgroundColor = chipBgColor
            chip.isCheckable = true
            chip.text = getString(
                R.string.download_format_s_dxd,
                format.format.format.uppercase(),
                format.width,
                format.height
            )
            // Selection listener
            chip.setOnCheckedChangeListener { _, isChecked ->
                format.isSelected = isChecked
                // Hide selection warning, if visible
                binding.noSelectionWarning.isVisible = false
            }

            binding.downloadFormatChipGroup.addView(chip)
        }

        // Setup views here
        binding.download.setOnClickListener {
            downloadSelectedFormats()
        }
        // Click listeners
        binding.close.setOnClickListener(this)
    }

    private fun downloadSelectedFormats() {
        // Download selected formats
        var isNoFormatSelected = true

        for (format in downloadFormats) {
            if (format.isSelected) {
                downloadHelper.downloadIcon(format)
                isNoFormatSelected = false
            }
        }

        if (isNoFormatSelected) {
            // Show warning
            binding.noSelectionWarning.isVisible = true
        } else {
            // Show toast
            requireContext().showShortToast(
                getString(R.string.msg_downloading)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.close -> {
                dismissAllowingStateLoss()
            }
        }
    }

    companion object {
        private const val ARG_DOWNLOAD_FORMATS = "download_formats"

        fun show(fm: FragmentManager, downloadFormats: ArrayList<Icon.DownloadFormat>) {
            val fragment = DownloadIconBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        ARG_DOWNLOAD_FORMATS,
                        downloadFormats
                    )
                }
            }
            fragment.show(fm, DownloadIconBottomSheet::class.java.simpleName)
        }
    }
}