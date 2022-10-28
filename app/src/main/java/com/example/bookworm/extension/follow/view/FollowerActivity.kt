package com.example.bookworm.extension.follow.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.example.bookworm.extension.follow.modules.FollowPagerAdapter
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
    private val followViewModel by lazy {
        ViewModelProvider(this, FollowViewModel.Factory(this)).get(FollowViewModel::class.java)
    }
    private val selected by lazy { intent.getIntExtra("page", 0) }
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
                    followViewModel.getUser(this, token!!) //현재 유저의 값 가져오는 것
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