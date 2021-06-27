package com.greedygames.geticons.ui.main.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.greedygames.geticons.R
import com.greedygames.geticons.data.models.Icon
import com.greedygames.geticons.databinding.FragmentIconSearchBinding
import com.greedygames.geticons.ui.adapters.IconListAdapter
import com.greedygames.geticons.ui.main.dialogs.IconSearchFiltersBottomSheet
import com.greedygames.geticons.ui.icon.IconDetailsActivity
import com.greedygames.geticons.utils.SnackbarHelper
import com.greedygames.geticons.viewmodels.IconSearchViewModel
import com.greedygames.geticons.viewmodels.IconSearchViewModel.IconListData

class IconSearchFragment : Fragment(), IconListAdapter.ItemRequestListener,
    IconListAdapter.ItemClickListener {

    // Valid between onCreateView and onDestroy.
    private var _binding: FragmentIconSearchBinding? = null
    private val binding get() = _binding!!

    private val iconListLayout get() = _binding!!.iconListLayout

    private val viewModel: IconSearchViewModel by activityViewModels()

    private lateinit var iconListAdapter: IconListAdapter

    private lateinit var snackbarListener: SnackbarHelper.SnackbarListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Try to attach SnackBarListener.
        try {
            snackbarListener = context as SnackbarHelper.SnackbarListener
        } catch (e: Exception) {
            throw RuntimeException(
                "Parent activity must implement " +
                        "${SnackbarHelper.SnackbarListener::class.simpleName}!"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentIconSearchBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        subscribeObservers()
    }

    private fun setupViews() {
        // Empty message.
        iconListLayout.emptyMessageLayout.textEmptyMessage.text =
            getString(R.string.no_icon_found)

        // Icons recycler view.
        iconListAdapter = IconListAdapter(
            this,
            this
        )
        val iconsRecyclerView = iconListLayout.icons
        iconsRecyclerView.layoutManager = GridLayoutManager(
            requireContext(),
            2
        )
        iconsRecyclerView.adapter = iconListAdapter

        // Listeners
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                // Request search.
                viewModel.searchIcons(query)
                return true
            }
        })
        // Click listeners
        iconListLayout.retryLayout.retry.setOnClickListener {
            viewModel.retry()
        }
        binding.searchFilter.setOnClickListener {
            // Show filter options.
            IconSearchFiltersBottomSheet().show(
                childFragmentManager,
                IconSearchFiltersBottomSheet::class.simpleName
            )
        }
    }

    private fun subscribeObservers() {
        // Observe changes to LiveData here.
        viewModel.searchResult.observe(viewLifecycleOwner, { data ->
            // Hide progress.
            hideShimmer()
            // Hide empty status, if visible.
            iconListLayout.emptyMessageLayout.isEmpty = false
            // Hide retry, is visible.
            iconListLayout.retryLayout.isRetry = false

            when (data) {
                is IconListData.Loading -> {
                    if (data.isFreshSearch) {
                        showShimmer()
                        // Clear list.
                        iconListAdapter.submitList(null)
                    }
                }
                is IconListData.Success -> {
                    // Empty status.
                    iconListLayout.emptyMessageLayout.isEmpty =
                        data.newList.isEmpty()
                    iconListAdapter.submitList(data.newList)
                }
                is IconListData.Error -> {
                    // Disable list progress, if enabled.
                    iconListAdapter.disableProgress()

                    if (data.currentList.isEmpty()) {
                        // Show retry.
                        iconListLayout.retryLayout.isRetry = true
                    } else {
                        // Show error message.
                        snackbarListener.onError(data.code)
                    }
                }
            }
        })
    }

    private fun showShimmer() {
        iconListLayout.isShimmer = true
        iconListLayout.shimmerProgress.startShimmer()
        // Reset scroll state
        iconListLayout.shimmerPlaceholderLayout.nestedScrollView.scrollTo(0, 0)
    }

    private fun hideShimmer() {
        iconListLayout.isShimmer = false
        iconListLayout.shimmerProgress.stopShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMoreItemsRequested() {
        viewModel.loadMoreItems()
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
}