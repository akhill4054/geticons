package com.greedygames.geticons.ui.author

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.greedygames.geticons.ERROR_TYPICAL
import com.greedygames.geticons.R
import com.greedygames.geticons.data.models.Author
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.databinding.ActivityAuthorDetailsBinding
import com.greedygames.geticons.ui.adapters.IconSetListAdapter
import com.greedygames.geticons.ui.dialogs.IconSetInfoPopup
import com.greedygames.geticons.ui.iconset.IconSetDetailsActivity
import com.greedygames.geticons.utils.SnackbarHelper
import com.greedygames.geticons.viewmodels.AuthorDetailsViewModel
import com.greedygames.geticons.viewmodels.AuthorDetailsViewModel.AuthorDetailsResponse
import com.greedygames.geticons.viewmodels.AuthorDetailsViewModel.AuthorIconSetsResponse

class AuthorDetailsActivity : AppCompatActivity(), IconSetListAdapter.ItemRequestListener,
    IconSetListAdapter.ItemClickListener {

    private lateinit var binding: ActivityAuthorDetailsBinding

    private val viewModel: AuthorDetailsViewModel by viewModels {
        AuthorDetailsViewModel.AuthorDetailsViewModelFactory(
            application,
            authorId = authorId,
            userId = userId
        )
    }

    private var authorId: Int? = null
    private var userId: Int? = null

    private lateinit var iconSetListAdapter: IconSetListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_author_details
        )

        // Getting passed args
        intent.getIntExtra(ARG_AUTHOR_ID, -1).let {
            authorId = if (it == -1) null else it
        }
        intent.getIntExtra(ARG_USER_ID, -1).let {
            userId = if (it == -1) null else it
        }

        setupViews()
        subscribeObservers()
    }

    private fun setupViews() {
        // Icons list recycler view
        iconSetListAdapter = IconSetListAdapter(
            this,
            this
        )
        binding.authorIconSets.adapter = iconSetListAdapter

        // Click listeners
        binding.retryLayout.retry.setOnClickListener {
            viewModel.retry()
        }
        binding.iconSetsRetry.retry.setOnClickListener {
            // Show shimmer
            showIconSetListShimmer()
            // Show icons list shimmer
            viewModel.loadIconSets()
        }
        binding.navigateUp.setOnClickListener {
            finish()
        }
        // Temporary workaround!
        binding.navigateUpOnProgress.setOnClickListener {
            finish()
        }
    }

    private fun subscribeObservers() {
        viewModel.authorDetails.observe(this, { data ->
            // Hide progress
            binding.isLoading = false
            // Hide retry
            binding.retryLayout.isRetry = false

            when (data) {
                is AuthorDetailsResponse.Loading -> {
                    binding.isLoading = true
                    showIconSetListShimmer()
                }
                is AuthorDetailsResponse.Success -> {
                    binding.author = data.author
                }
                else -> {
                    // Error
                    binding.retryLayout.isRetry = true
                }
            }
        })
        viewModel.authorIconSets.observe(this, { data ->
            // Hide shimmer, if visible
            hideIconListShimmer()
            // Hide retry, if visible
            binding.retryLayout.isRetry = false

            when (data) {
                is AuthorIconSetsResponse.Loading -> {
                    if (data.currentList.isEmpty()) {
                        showIconSetListShimmer()
                    }
                }
                is AuthorIconSetsResponse.Success -> {
                    iconSetListAdapter.submitList(data.newList)
                }
                is AuthorIconSetsResponse.Error -> {
                    if (data.currentList.isEmpty()) {
                        // Show retry
                        binding.iconSetsRetry.isRetry = true
                    } else {
                        // Show error message
                        SnackbarHelper.showError(
                            binding.coordinatorLayout,
                            ERROR_TYPICAL
                        )
                        iconSetListAdapter.disableProgress()
                    }
                }
            }
        })
    }

    private fun showIconSetListShimmer() {
        binding.isIconSetListLoading = true
        binding.iconSetListShimmerView.startShimmer()
    }

    private fun hideIconListShimmer() {
        binding.isIconSetListLoading = false
        binding.iconSetListShimmerView.stopShimmer()
    }

    override fun onMoreItemsRequested() {
        viewModel.loadIconSets()
    }

    override fun onIconSetItemClicked(view: View, clickId: Int, iconSet: IconSet) {
        when (clickId) {
            IconSetListAdapter.CLICK_ID_SHOW_INFO -> {
                IconSetInfoPopup(this, iconSet)
                    .show(view)
            }
            else -> {
                IconSetDetailsActivity.launch(this, iconSet)
            }
        }
    }

    companion object {
        private const val ARG_AUTHOR_ID = "author_id"
        private const val ARG_USER_ID = "author_user_id"

        fun launch(context: Context, author: Author) {
            val intent = Intent(context, AuthorDetailsActivity::class.java).apply {
                if (author.authorId != null) {
                    putExtra(ARG_AUTHOR_ID, author.authorId)
                } else {
                    putExtra(ARG_USER_ID, author.userId)
                }
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }
    }
}