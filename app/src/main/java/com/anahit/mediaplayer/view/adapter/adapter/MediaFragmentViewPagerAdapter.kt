package com.anahit.mediaplayer.view.adapter.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

//TODO: [FragmentPagerAdapter] is deprecated, please fix it
class MediaFragmentViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private var fragments: ArrayList<Fragment> = arrayListOf()
    private var titles: ArrayList<String> = arrayListOf()

    override fun getItemCount(): Int {
       return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
      return fragments[position]
    }

        fun addFragments(fragment: Fragment, title: String){
        fragments.add(fragment)
        titles.add(title)
    }

        fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}
