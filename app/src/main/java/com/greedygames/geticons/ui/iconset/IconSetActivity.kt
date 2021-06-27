package com.greedygames.geticons.ui.iconset

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.greedygames.geticons.ERROR_TYPICAL
import com.greedygames.geticons.R
import com.greedygames.geticons.data.models.Icon
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.databinding.ActivityIconSetBinding
import com.greedygames.geticons.ui.adapters.IconListAdapter
import com.greedygames.geticons.utils.SnackbarHelper
import com.greedygames.geticons.viewmodels.IconSetViewModel
import com.greedygames.geticons.viewmodels.IconSetViewModel.IconListResponse
import com.greedygames.geticons.viewmodels.IconSetViewModel.IconSetDetailsResponse

class IconSetActivity : AppCompatActivity(), IconListAdapter.ItemRequestListener,
    IconListAdapter.ItemClickListener {

    private lateinit var binding: ActivityIconSetBinding

    private val viewModel: IconSetViewModel by viewModels {
        IconSetViewModel.IconSetViewModelFactory(
            application,
            iconSetId
        )
    }

    private var iconSetId = -1

    private lateinit var iconListAdapter: IconListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_icon_set
        )

        iconSetId = intent.getIntExtra(ARG_ICON_SET_ID, -1)

        setupViews()
        subscribeObservers()
    }

    private fun setupViews() {
        // Icons list recycler view
        iconListAdapter = IconListAdapter(
            this,
            this
        )
        val iconsRecyclerView = binding.layoutIcons.icons
        iconsRecyclerView.adapter = iconListAdapter
        iconsRecyclerView.layoutManager = GridLayoutManager(this, 2)

        // Click listeners
        binding.retryLayout.retry.setOnClickListener {
            viewModel.retry()
        }
        binding.layoutIcons.retryLayout.retry.setOnClickListener {
            // Show shimmer
            showIconListShimmer()
            // Show icons list shimmer
            viewModel.retry()
        }
        binding.author.setOnClickListener {
            // Navigate to author details screen
        }
        binding.navigateUp.setOnClickListener {
            onBackPressed()
        }
    }

    private fun subscribeObservers() {
        viewModel.iconSetDetailsData.observe(this, { data ->
            // Hide progress
            binding.isLoading = false
            // Hide retry
            binding.retryLayout.isRetry = false

            when (data) {
                is IconSetDetailsResponse.Loading -> {
                    binding.isLoading = true
                    showIconListShimmer()
                }
                is IconSetDetailsResponse.Success -> {
                    binding.iconSetDetails = data.details
                }
                else -> {
                    // Error
                    binding.retryLayout.isRetry = true
                }
            }
        })
        viewModel.iconListData.observe(this, { data ->
            // Hide retry, if visible
            binding.layoutIcons.retryLayout.isRetry = false
            // Hide shimmer, if visible
            hideIconListShimmer()

            when (data) {
                is IconListResponse.Loading -> {
                    if (data.currentList.isEmpty()) {
                        showIconListShimmer()
                    }
                }
                is IconListResponse.Success -> {
                    hideIconListShimmer()
                    iconListAdapter.submitList(data.newList)
                }
                is IconListResponse.Error -> {
                    if (data.currentList.isEmpty()) {
                        // Show retry
                        binding.layoutIcons.retryLayout.isRetry = true
                    } else {
                        // Show error message
                        SnackbarHelper.showError(
                            binding.coordinatorLayout,
                            ERROR_TYPICAL
                        )
                        iconListAdapter.disableProgress()
                    }
                }
            }
        })
    }

    private fun showIconListShimmer() {
        binding.layoutIcons.isShimmer = true
        binding.layoutIcons.shimmerProgress.startShimmer()
    }

    private fun hideIconListShimmer() {
        binding.layoutIcons.isShimmer = false
        binding.layoutIcons.shimmerProgress.stopShimmer()
    }

    override fun onMoreItemsRequested() {
        viewModel.loadMoreIcons()
    }

    override fun onIconSetItemClicked(clickId: Int, icon: Icon) {

    }

    companion object {
        private const val ARG_ICON_SET_ID = "icon_set_id"

        fun launch(f: Fragment, iconSet: IconSet) {
            val context = f.requireContext()

            val intent = Intent(context, IconSetActivity::class.java).apply {
                putExtra(ARG_ICON_SET_ID, iconSet.id)
            }
            context.startActivity(intent)
        }
    }
}