package com.greedygames.geticons.ui.iconset

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.greedygames.geticons.R
import com.greedygames.geticons.data.models.Icon
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.databinding.ActivityIconSetDetailsBinding
import com.greedygames.geticons.ui.adapters.IconListAdapter
import com.greedygames.geticons.ui.author.AuthorDetailsActivity
import com.greedygames.geticons.ui.dialogs.LicenseInfoPopup
import com.greedygames.geticons.ui.icon.IconDetailsActivity
import com.greedygames.geticons.utils.SnackbarHelper
import com.greedygames.geticons.viewmodels.IconSetViewModel
import com.greedygames.geticons.viewmodels.IconSetViewModel.IconSetDetailsResponse
import com.greedygames.geticons.viewmodels.IconSetViewModel.IconSetIconsResponse

class IconSetDetailsActivity : AppCompatActivity(), IconListAdapter.ItemRequestListener,
    IconListAdapter.ItemClickListener, View.OnClickListener {

    private lateinit var binding: ActivityIconSetDetailsBinding

    private val iconSetLayout get() = binding.iconSetDetailsLayout

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
            R.layout.activity_icon_set_details
        )

        iconSetId = intent.getIntExtra(ARG_ICON_SET_ID, -1)

        setupViews()
        subscribeObservers()
    }

    private fun setupViews() {
        // Icons list recycler view
        iconListAdapter = IconListAdapter(
            this,
            this,
            false
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
        iconSetLayout.textAuthorName.setOnClickListener(this)
        iconSetLayout.info.setOnClickListener(this)
        binding.navigateUp.setOnClickListener(this)
        // Temporary workaround
        binding.navigateUpOnProgress.setOnClickListener {
            finish()
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
                    iconSetLayout.iconSet = data.details
                }
                else -> {
                    // Error
                    binding.retryLayout.isRetry = true
                }
            }
        })
        viewModel.iconSetIconsData.observe(this, { data ->
            // Hide retry, if visible
            binding.layoutIcons.retryLayout.isRetry = false
            // Hide shimmer, if visible
            hideIconListShimmer()

            when (data) {
                is IconSetIconsResponse.Loading -> {
                    showIconListShimmer()
                }
                is IconSetIconsResponse.Success -> {
                    hideIconListShimmer()
                    iconListAdapter.submitList(data.icons)
                }
                is IconSetIconsResponse.Error -> {
                    // Show retry
                    binding.layoutIcons.retryLayout.isRetry = true
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
        when (clickId) {
            IconListAdapter.CLICK_ID_DOWNLOAD -> {

            }
            else -> {
                IconDetailsActivity.launch(this, icon)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.info -> {
                val license = iconSetLayout.iconSet!!.getFinalLicense()
                if (license != null) {
                    LicenseInfoPopup(this, license).show(v)
                } else {
                    SnackbarHelper.showSnackbar(
                        binding.coordinatorLayout,
                        getString(R.string.msg_no_info)
                    )
                }
            }
            R.id.navigate_up -> {
                finish()
            }
            R.id.text_author_name -> {
                // Navigate to author details screen
                AuthorDetailsActivity.launch(
                    this,
                    binding.iconSetDetailsLayout.iconSet!!.author
                )
            }
        }
    }

    companion object {
        private const val ARG_ICON_SET_ID = "icon_set_id"

        fun launch(f: Fragment, iconSet: IconSet) {
            launch(f.requireContext(), iconSet)
        }

        fun launch(context: Context, iconSet: IconSet) {
            val intent = Intent(context, IconSetDetailsActivity::class.java).apply {
                putExtra(ARG_ICON_SET_ID, iconSet.id)
            }
            context.startActivity(intent)
        }
    }
}