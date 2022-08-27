package com.example.bookworm.bottomMenu.profile.submenu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.bookworm.bottomMenu.profile.submenu.views.FragmentAlbums
import com.example.bookworm.bottomMenu.profile.submenu.views.FragmentPosts

class SubMenuPagerAdapter : FragmentStatePagerAdapter {
    private val pageList = ArrayList<Fragment>()

    constructor(token: String?, fragmentManager: FragmentManager) : super(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        pageList.add(FragmentPosts(token)) //포스트 탭
        pageList.add(FragmentAlbums(token)) //앨범 탭
    }

    override fun getCount(): Int {
        return pageList.size
    }

    override fun getItem(position: Int): Fragment {
        return pageList[position]
    }

}