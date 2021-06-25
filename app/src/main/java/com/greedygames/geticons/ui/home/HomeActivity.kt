package com.greedygames.geticons.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.greedygames.geticons.ERROR_BAD_RESPONSE
import com.greedygames.geticons.ERROR_NO_INTERNET
import com.greedygames.geticons.R
import com.greedygames.geticons.databinding.ActivityHomeBinding
import com.greedygames.geticons.ui.home.fragments.IconSetListFragment
import com.greedygames.geticons.ui.home.fragments.IconsFragment
import com.greedygames.geticons.utils.interfaces.SnackbarListener

class HomeActivity : AppCompatActivity(), SnackbarListener {

    private lateinit var binding: ActivityHomeBinding

    private val viewPager get() = binding.viewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        // ViewPager
        val pagerAdapter = HomePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false

        // Setup tabLayout with viewPager
        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            tab.text = pagerAdapter.titles[position]
        }.attach()
    }

    override fun onBackPressed() {
        if (viewPager.currentItem > 0) {
            // Go back to previous tab.
            viewPager.currentItem = viewPager.currentItem - 1
        } else {
            // Close the activity.
            super.onBackPressed()
        }
    }

    private fun showSnackbar(
        message: String,
        anchorView: View?,
        isError: Boolean = false
    ) {
        val sb = Snackbar.make(
            binding.coordinatorLayout,
            message,
            Snackbar.LENGTH_SHORT
        )
        if (anchorView != null) {
            sb.anchorView = anchorView
        }
        if (isError) {
            sb.setTextColor(Color.WHITE)
            sb.setBackgroundTint(ContextCompat.getColor(this, R.color.error))
        }
        sb.show()
    }

    // SnackBar listener callbacks.
    override fun onShowSnackbar(message: String, anchorView: View?) {
        showSnackbar(message, anchorView)
    }

    override fun onError(code: Int, anchorView: View?) {
        showSnackbar(
            when (code) {
                ERROR_NO_INTERNET -> {
                    getString(R.string.warn_no_internet)
                }
                ERROR_BAD_RESPONSE -> {
                    getString(R.string.err_bad_response)
                }
                else -> {
                    getString(R.string.err_typical)
                }
            },
            anchorView = anchorView,
            isError = true
        )
    }

    private inner class HomePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        private val fragments = listOf(
            IconSetListFragment(),
            IconsFragment()
        )

        val titles = listOf(
            getString(R.string.tab_icon_sets),
            getString(R.string.tab_icons)
        )

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}