package com.example.bookworm.bottomMenu.profile.Album.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.profile.Album.item.albumViewModel
import com.example.bookworm.databinding.ActivityCreateAlbumBinding

class CreateAlbumActivity : AppCompatActivity() {
    lateinit var selectedFeed: ArrayList<Feed>
    var binding: ActivityCreateAlbumBinding? = null
    lateinit var albumViewModel: albumViewModel
    val fm: FragmentManager = supportFragmentManager
    lateinit var fragmentSelectfeed: fragment_selectFeed
    lateinit var fragmentAlbumArt: fragment_albumArt
    lateinit var fragmentAlbumname: fragment_albumName
    lateinit var feedList: ArrayList<Feed>

    //시작
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateAlbumBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        selectedFeed = ArrayList()
        albumViewModel = ViewModelProvider(this).get()
        feedList= intent.getSerializableExtra("list") as ArrayList<Feed> //사용자가 작성한 피드의 목록을 보여줌
        //생성
        fragmentSelectfeed = fragment_selectFeed()
        fragmentAlbumArt = fragment_albumArt()
        fragmentAlbumname = fragment_albumName()
        fm.beginTransaction().add(R.id.albumContainer, fragmentAlbumname, "0")
            .add(R.id.albumContainer, fragmentAlbumArt, "1")
            .add(R.id.albumContainer, fragmentSelectfeed, "2")
            .commitAllowingStateLoss()

        //열기
        switchTab(0)
    }

    fun switchTab(idx: Int) {
        when (idx) {
            0 -> {
                fm.beginTransaction()
                    .show(fragmentAlbumname)
                    .hide(fragmentAlbumArt)
                    .hide(fragmentSelectfeed)
                    .commitAllowingStateLoss()
                albumViewModel.updateAlbum()
            }
            1->{
                fm.beginTransaction()
                    .show(fragmentAlbumArt)
                    .hide(fragmentAlbumname)
                    .hide(fragmentSelectfeed)
                    .commitAllowingStateLoss()
                albumViewModel.updateAlbum()
            }
            else->{
                fm.beginTransaction()
                    .show(fragmentSelectfeed)
                    .hide(fragmentAlbumname)
                    .hide(fragmentAlbumArt)
                    .commitAllowingStateLoss()
                albumViewModel.updateAlbum()
            }
        }

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}