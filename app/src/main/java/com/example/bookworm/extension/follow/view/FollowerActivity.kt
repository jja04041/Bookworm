package com.example.bookworm.extension.follow.view

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import android.os.Bundle
import com.example.bookworm.R
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.bookworm.extension.follow.modules.FollowPagerAdapter
import com.example.bookworm.extension.follow.view.FollowViewModelImpl
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.ActivityFollowerBinding

class FollowerActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityFollowerBinding.inflate(layoutInflater)
    }
    val token by lazy {
        intent.getStringExtra("token")
    }
    val followViewModel by lazy {
        ViewModelProvider(this, FollowViewModelImpl.Factory(this)).get(FollowViewModelImpl::class.java)
    }
    val selected by lazy { intent.getIntExtra("page", 0) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val adapter = FollowPagerAdapter(supportFragmentManager, token)
        binding.apply {
            viewpager.apply {
                viewpager.adapter = adapter
            }

            //탭 레이아웃 구성
            tabLayout.apply {
                setupWithViewPager(viewpager)
                getTabAt(selected)!!.select()
                //팔로잉 팔로워가 변하면 데이터를 인식함
                MutableLiveData<UserInfo>().apply {
                    followViewModel.getUser(this, token!!)
                    observe(this@FollowerActivity) { userdata ->
                        userdata.apply {
                            getTabAt(0)!!.text = "$followerCounts 팔로워"
                            getTabAt(1)!!.text = "$followingCounts 팔로잉"
                        }
                    }
                }
            }
        }
    }
}