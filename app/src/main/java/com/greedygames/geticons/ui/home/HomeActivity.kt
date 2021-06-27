package com.greedygames.geticons.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.greedygames.geticons.R
import com.greedygames.geticons.databinding.ActivityHomeBinding
import com.greedygames.geticons.ui.home.fragments.IconSearchFragment
import com.greedygames.geticons.ui.home.fragments.IconSetListFragment
import com.greedygames.geticons.utils.SnackbarHelper

class HomeActivity() : AppCompatActivity(), SnackbarHelper.SnackbarListener {

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

    // SnackBar listener callbacks.
    override fun onShowSnackbar(message: String, anchorView: View?) {
        SnackbarHelper.showSnackbar(
            binding.coordinatorLayout,
            message,
            anchorView
        )
    }

    override fun onError(code: Int, anchorView: View?) {
        SnackbarHelper.showError(
            binding.root,
            code,
            anchorView
        )
    }

    private inner class HomePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        private val fragments = listOf(
            IconSetListFragment(),
            IconSearchFragment()
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