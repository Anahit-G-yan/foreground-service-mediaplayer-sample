package com.anahit.mediaplayer.feature.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.anahit.mediaplayer.feature.library.musiclist.MusicListFragment
import com.anahit.mediaplayer.feature.library.videolist.VideoListFragment

private const val TAB_COUNT = 2
private const val TAB_MUSIC = 0

class LibraryPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = TAB_COUNT

    override fun createFragment(position: Int): Fragment =
        if (position == TAB_MUSIC) MusicListFragment() else VideoListFragment()
}
