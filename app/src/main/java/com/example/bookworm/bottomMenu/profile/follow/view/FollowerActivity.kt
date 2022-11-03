package com.example.bookworm.bottomMenu.profile.follow.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.example.bookworm.bottomMenu.profile.follow.modules.FollowPagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.follow.modules.FollowViewModel
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.ActivityFollowerBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
 *팔로잉 팔로워 목록을 띄워주는 Activity
 */

class FollowerActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityFollowerBinding.inflate(layoutInflater)
    }
    val token by lazy {
        intent.getStringExtra("token")!!
    }
    private val followViewModel by lazy {
        ViewModelProvider(this, FollowViewModel.Factory(this))[FollowViewModel::class.java]
    }
    private val userInfoViewModel by lazy {
        ViewModelProvider(this, UserInfoViewModel.Factory(this))[UserInfoViewModel::class.java]
    }
    private val targetUserInfoLiveData = MutableLiveData<UserInfo>()
    private val nowUserInfoLiveData = MutableLiveData<UserInfo>()
    private val selected by lazy { intent.getIntExtra("page", 0) }
    lateinit var adapter: FollowPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //입력된 유저 정보를 가져오는 부분
        userInfoViewModel.getUser(token = token, liveData = targetUserInfoLiveData, true)

        //데이터가 받아져왔을 때
        targetUserInfoLiveData.observe(this) { targetUserData ->
            if (targetUserData != null) {
                userInfoViewModel.getUser(null, nowUserInfoLiveData, true) //현재 사용중인 유저의 정보를 가져오는 부분
                nowUserInfoLiveData.observe(this@FollowerActivity) { nowUserData ->
                    if (nowUserData != null) {
                        setUI(targetUser = targetUserData, nowUser = nowUserData)
                    }
                }
            }
        }
    }

    //targetUser: 팔로잉, 팔로워 탭을 보고자 하는 대상의 정보 , nowUser: 팔로잉, 팔로워 탭을 보려고 하는 주체(현재 사용자 정보)
    private fun setUI(targetUser: UserInfo, nowUser: UserInfo) {
        adapter = FollowPagerAdapter(
            supportFragmentManager,
            lifecycle,
            targetUser,
            nowUser
        )
        binding.apply {
            viewpager.adapter = adapter
            TabLayoutMediator(
                tabLayout,
                viewpager,
                false,
                true
            ) { tab, position ->
                //팔로잉 팔로워가 변하면 데이터를 인식함
                targetUser.apply {
                    tab.text = if (position == 0) "$followerCounts 팔로워"
                    else "$followingCounts 팔로잉"
                    tab.tag = position.toString()
                }
            }.attach()
            tabLayout.getTabAt(selected)!!.select()
            setContentView(binding.root) //로드가 다 된후 레이아웃을 보여줌
            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}
