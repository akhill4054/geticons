package com.greedygames.geticons.ui.main.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.greedygames.geticons.R
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.databinding.FragmentIconSetListBinding
import com.greedygames.geticons.ui.adapters.IconSetListAdapter
import com.greedygames.geticons.ui.dialogs.IconSetInfoPopup
import com.greedygames.geticons.ui.iconset.IconSetDetailsActivity
import com.greedygames.geticons.utils.SnackbarHelper
import com.greedygames.geticons.viewmodels.IconSetListViewModel
import com.greedygames.geticons.viewmodels.IconSetListViewModel.IconSetListData

class IconSetListFragment : Fragment(), IconSetListAdapter.ItemRequestListener,
    IconSetListAdapter.ItemClickListener {

    private var _binding: FragmentIconSetListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: IconSetListViewModel by viewModels()

    private lateinit var iconSetListAdapter: IconSetListAdapter

    private lateinit var snackbarListener: SnackbarHelper.SnackbarListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Try to attach to Snackbar listener.
        try {
            snackbarListener = context as SnackbarHelper.SnackbarListener
        } catch (e: Exception) {
            throw RuntimeException(
                "${context::class.simpleName} must implement " +
                        "${SnackbarHelper.SnackbarListener::class.simpleName}!"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment.
        _binding = FragmentIconSetListBinding.inflate(
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
        // Setup view here.
        // Empty list message.
        binding.emptyMessageLayout.textEmptyMessage.text = getString(R.string.no_icon_sets)
        // Show shimmer.
        binding.isShimmer = true

        // IconSet list recyclerView.
        iconSetListAdapter = IconSetListAdapter(
            this,
            this
        )
        binding.iconSetList.adapter = iconSetListAdapter

        // Click listeners
        binding.retryLayout.retry.setOnClickListener {
            // Hide retry.
            binding.retryLayout.isRetry = false
            // Show shimmer.
            binding.isShimmer = true
            binding.shimmerProgress.startShimmer()
            // Try to reload.
            viewModel.retry()
        }
    }

    private fun subscribeObservers() {
        // Observe liveData here.
        viewModel.iconSetListData.observe(viewLifecycleOwner, { data ->
            if (data !is IconSetListData.FetchIsInProgress) {
                // Hide empty message, if visible.
                binding.emptyMessageLayout.isEmpty = false
                // Stop shimmer.
                binding.shimmerProgress.stopShimmer()
                binding.isShimmer = false
                // Hide error, if visible.
                binding.retryLayout.isRetry = false
            }

            when (data) {
                is IconSetListData.Success -> {
                    // Update binding object.
                    binding.emptyMessageLayout.isEmpty = data.newList.isEmpty()
                    // Update list.
                    iconSetListAdapter.submitList(data.newList)
                }
                is IconSetListData.Error -> {
                    // Disable pagination progress. if enables.
                    iconSetListAdapter.disableProgress()

                    if (data.currentList.isEmpty()) {
                        // Show retry
                        binding.retryLayout.isRetry = true
                    } else {
                        // Show error message
                        snackbarListener.onError(data.code)
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // List adapter listener callbacks.
    override fun onMoreItemsRequested() {
        viewModel.loadMoreItems()
    }

    override fun onIconSetItemClicked(view: View, clickId: Int, iconSet: IconSet) {
        if (clickId == IconSetListAdapter.CLICK_ID_SHOW_DETAILS) {
            // Navigate to icon set details screen.
            IconSetDetailsActivity.launch(this, iconSet)
        } else { // Show info.
            IconSetInfoPopup(requireContext(), iconSet)
                .show(view)
        }
    }
}