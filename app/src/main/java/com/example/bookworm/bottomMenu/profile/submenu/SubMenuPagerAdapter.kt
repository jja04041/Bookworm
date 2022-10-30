package com.example.bookworm.bottomMenu.profile.submenu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.bookworm.bottomMenu.profile.submenu.views.FragmentAlbums
import com.example.bookworm.bottomMenu.profile.submenu.views.FragmentPosts

class SubMenuPagerAdapter//포스트 탭
//앨범 탭
(token: String?, fragmentManager: FragmentManager) : FragmentStatePagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val pageList = ArrayList<Fragment>()

    init {
        pageList.add(FragmentPosts(token))
        pageList.add(FragmentAlbums(token))
    }

    override fun getCount(): Int {
        return pageList.size
    }

    override fun getItem(position: Int): Fragment {
        return pageList[position]
    }

}