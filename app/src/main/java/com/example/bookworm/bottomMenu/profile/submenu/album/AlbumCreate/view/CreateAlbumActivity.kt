package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view

import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.item.AlbumProcessViewModel
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.databinding.ActivityCreateAlbumBinding


class CreateAlbumActivity : AppCompatActivity() {
    lateinit var selectedFeed: ArrayList<Feed>
    var binding: ActivityCreateAlbumBinding? = null
    lateinit var albumProcessViewModel: AlbumProcessViewModel
    val fm: FragmentManager = supportFragmentManager
    lateinit var fragmentSelectfeed: FragmentSelectFeed //앨범에 들어갈 피드를 선택
    lateinit var fragmentAlbumArt: FragmentAlbumArt //앨범의 커버를 선택
    lateinit var fragmentAlbumname: FragmentAlbumName //앨범의 이름을 지어줌
    lateinit var feedList: ArrayList<Feed>
    lateinit var imageProcessing: ImageProcessing

    //시작
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAlbumBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        var pv: UserInfoViewModel =
            ViewModelProvider(
                this,
                UserInfoViewModel.Factory(this)
            ).get(
                UserInfoViewModel::class.java
            )
        imageProcessing = ImageProcessing(this)
        selectedFeed = ArrayList()
        albumProcessViewModel =
            ViewModelProvider(this, AlbumProcessViewModel.Factory(this, pv)).get(
                AlbumProcessViewModel::class.java
            )
        feedList = intent.getSerializableExtra("list") as ArrayList<Feed> //사용자가 작성한 피드의 목록을 보여줌

        //생성
        fragmentSelectfeed = FragmentSelectFeed()
        fragmentAlbumArt = FragmentAlbumArt()
        fragmentAlbumname = FragmentAlbumName()
        fm.beginTransaction().add(R.id.albumContainer, fragmentAlbumname, "0")
            .add(R.id.albumContainer, fragmentAlbumArt, "1")
            .add(R.id.albumContainer, fragmentSelectfeed, "2")
            .commitAllowingStateLoss()
        //열기
        switchTab(0)
    }

    //탭을 변경
    fun switchTab(idx: Int) {
        when (idx) {
            0 -> {
                fm.beginTransaction()
                    .show(fragmentAlbumname)
                    .hide(fragmentAlbumArt)
                    .hide(fragmentSelectfeed)
                    .commitAllowingStateLoss()
                albumProcessViewModel.updateAlbum()
            }
            1 -> {
                fm.beginTransaction()
                    .show(fragmentAlbumArt)
                    .hide(fragmentAlbumname)
                    .hide(fragmentSelectfeed)
                    .commitAllowingStateLoss()
                albumProcessViewModel.updateAlbum()
            }
            else -> {
                fm.beginTransaction()
                    .show(fragmentSelectfeed)
                    .hide(fragmentAlbumname)
                    .hide(fragmentAlbumArt)
                    .commitAllowingStateLoss()
                albumProcessViewModel.updateAlbum()
            }
        }

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

}