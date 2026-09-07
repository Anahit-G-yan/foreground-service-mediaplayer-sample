package com.anahit.mediaplayer.feature.library

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anahit.mediaplayer.core.ui.viewBinding
import com.anahit.mediaplayer.feature.library.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import com.anahit.mediaplayer.core.ui.R as CoreUiR

private const val TAB_MUSIC = 0

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {
    private val binding by viewBinding(FragmentLibraryBinding::bind)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val window = requireActivity().window
        val black = ContextCompat.getColor(requireContext(), CoreUiR.color.black)
        window.statusBarColor = black
        window.navigationBarColor = black

        binding.viewPager.adapter = LibraryPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == TAB_MUSIC) getString(R.string.tab_music) else getString(R.string.tab_video)
        }.attach()
    }
}
