package com.greedygames.geticons.ui.home.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.greedygames.geticons.R
import com.greedygames.geticons.databinding.BottomSheetIconSearchFiltersBinding
import com.greedygames.geticons.repositories.IconRepository.IconSearchFilter
import com.greedygames.geticons.viewmodels.IconSearchViewModel

class IconSearchFiltersBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetIconSearchFiltersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: IconSearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetIconSearchFiltersBinding.inflate(
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

        // Spinners
        // Premium filter options
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_by_premium_options,
            R.layout.spinner_list_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.premiumFilters.adapter = adapter
            // Item selection
            binding.premiumFilters.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        viewModel.updatePremiumType(
                            when (position) {
                                0 -> null // Not selected
                                1 -> false // Free
                                else -> true // Premium
                            }
                        )
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
        }

        // License filter options
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_by_license_options,
            R.layout.spinner_list_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.licenseFilter.adapter = adapter
            // Item selection
            binding.licenseFilter.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    viewModel.updateLicenseFilter(
                        when (position) {
                            0 -> IconSearchFilter.LICENSES_ALL // Not selected
                            1 -> IconSearchFilter.LICENSES_COMMERCIAL // Commercial
                            else -> IconSearchFilter.LICENCES_NO_ATTRIBUTION // No attribution
                        }
                    )
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }

        // Update views based on applied filters.
        updateViewsBySearchFilter(viewModel.searchFilter)

        // Click listeners
        binding.clearFilter.setOnClickListener {
            viewModel.clearFilter()
            // Update UI.
            updateViewsBySearchFilter(viewModel.searchFilter)
            dismissAllowingStateLoss()
        }
        binding.close.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun updateViewsBySearchFilter(filter: IconSearchFilter) {
        binding.premiumFilters.setSelection(
            when (filter.premium) {
                null -> 0 // Not selected
                false -> 1 // Free
                else -> 2 // Premium
            }
        )
        binding.licenseFilter.setSelection(
            when (filter.license) {
                IconSearchFilter.LICENSES_ALL -> 0 // Not selected
                IconSearchFilter.LICENSES_COMMERCIAL -> 1 // Commercial
                else -> 2 // No attribution
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}