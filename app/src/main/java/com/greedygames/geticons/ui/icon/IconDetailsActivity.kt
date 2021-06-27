package com.greedygames.geticons.ui.icon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.greedygames.geticons.ERROR_TYPICAL
import com.greedygames.geticons.R
import com.greedygames.geticons.data.models.Icon
import com.greedygames.geticons.databinding.ActivityIconDetailsBinding
import com.greedygames.geticons.ui.author.AuthorDetailsActivity
import com.greedygames.geticons.ui.dialogs.LicenseInfoPopup
import com.greedygames.geticons.utils.SnackbarHelper
import com.greedygames.geticons.viewmodels.IconDetailsViewModel
import com.greedygames.geticons.viewmodels.IconDetailsViewModel.IconDetailsResponse

class IconDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityIconDetailsBinding

    private val iconDetailsBinding get() = binding.iconSetDetailsLayout

    private val viewModel: IconDetailsViewModel by viewModels {
        IconDetailsViewModel.IconDetailsViewModelFactory(
            application,
            iconId
        )
    }

    private var iconId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_icon_details
        )

        // Passed arg
        iconId = intent.getIntExtra(ARG_ICON_ID, -1)

        setupViews()
        subscribeObservers()
    }

    private fun setupViews() {
        // Click listeners
        binding.navigateUp.setOnClickListener(this)
        binding.download.setOnClickListener(this)
        binding.retryLayout.retry.setOnClickListener(this)
        iconDetailsBinding.info.setOnClickListener(this)
        iconDetailsBinding.textAuthorName.setOnClickListener(this)
    }

    private fun subscribeObservers() {
        viewModel.iconDetails.observe(this, { data ->
            // Hide progress, if showing
            binding.isLoading = false
            // Hide retry, if visible
            binding.retryLayout.isRetry = false

            when (data) {
                is IconDetailsResponse.Loading -> {
                    binding.isLoading = true
                }
                is IconDetailsResponse.Success -> {
                    binding.icon = data.icon
                    iconDetailsBinding.iconSet = data.icon.requireIconSet()
                }
                else -> { // Error
                    binding.retryLayout.isRetry = true
                    SnackbarHelper.showError(
                        binding.coordinatorLayout,
                        ERROR_TYPICAL
                    )
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.navigate_up -> {
                finish()
            }
            R.id.text_author_name -> {
                AuthorDetailsActivity.launch(
                    this,
                    binding.icon!!.requireIconSet().author
                )
            }
            R.id.info -> {
                val license = iconDetailsBinding.iconSet!!.getFinalLicense()
                if (license != null) {
                    LicenseInfoPopup(this, license).show(v)
                } else {
                    SnackbarHelper.showSnackbar(
                        binding.coordinatorLayout,
                        getString(R.string.msg_no_info)
                    )
                }
            }
            R.id.download -> {

            }
            R.id.retry -> {
                viewModel.loadIconDetails()
            }
        }
    }

    companion object {
        private const val ARG_ICON_ID = "arg_icon_id"

        fun launch(f: Fragment, icon: Icon) {
            val context = f.requireContext()
            launch(context, icon)
        }

        fun launch(context: Context, icon: Icon) {
            val intent = Intent(context, IconDetailsActivity::class.java).apply {
                putExtra(ARG_ICON_ID, icon.id)
            }
            context.startActivity(intent)
        }
    }
}