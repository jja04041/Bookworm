package com.example.bookworm.bottomMenu.profile.submenu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class SubMenuPagerAdapter : FragmentStatePagerAdapter {
    private val pageList = ArrayList<Fragment>()
    var token: String? = null

    constructor(fragmentManager: FragmentManager) :
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)

    fun addToken(token: String) {
        this.token = token
    }

    init {
        pageList.add(FragmentPosts()) //포스트 탭
        pageList.add(FragmentAlbums()) //앨범 탭
    }

    override fun getCount(): Int {
        return pageList.size
    }

    override fun getItem(position: Int): Fragment {
        return pageList[position]
    }
}