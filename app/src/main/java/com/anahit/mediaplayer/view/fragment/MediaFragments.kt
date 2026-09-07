package com.anahit.mediaplayer.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.anahit.mediaplayer.R
import com.anahit.mediaplayer.view.adapter.adapter.MediaFragmentViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MediaFragments : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleStatusAndNavigationBarColor()  //for changing status and navigation bar colors
        initView()
        initViewPagerAdapter()
    }


    private fun handleStatusAndNavigationBarColor(){
        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.black)
        window.navigationBarColor = this.resources.getColor(R.color.black)
    }

    private fun initView() {
        viewPager = requireView().findViewById(R.id.viewPager)
        tabLayout = requireView().findViewById(R.id.tabLayout)
    }


    private fun initViewPagerAdapter() {
        val mediaFragmentViewPagerAdapter = MediaFragmentViewPagerAdapter(requireActivity())
        //TODO: please move all strings to string.xml file
        mediaFragmentViewPagerAdapter.addFragments(MusicFragment(), resources.getString(R.string.music))
        mediaFragmentViewPagerAdapter.addFragments(VideoFragment(), resources.getString(R.string.video))
        viewPager.adapter = mediaFragmentViewPagerAdapter
        //tabLayout set up with viewpager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = mediaFragmentViewPagerAdapter.getPageTitle(position)
        }.attach()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }
}