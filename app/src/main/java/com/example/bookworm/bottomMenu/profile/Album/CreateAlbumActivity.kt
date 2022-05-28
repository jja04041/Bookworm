package com.example.bookworm.bottomMenu.profile.Album

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.databinding.ActivityCreateAlbumBinding

class CreateAlbumActivity : AppCompatActivity() {
    lateinit var selectedFeed: ArrayList<Feed>
    var binding: ActivityCreateAlbumBinding? = null
    lateinit var newAlbumData: AlbumData
    val fm: FragmentManager = supportFragmentManager
    val fragmentSelectfeed = fragment_selectFeed()
    val fragmentAlbumart = fragment_albumArt()
    val fragmentAlbumname = fragment_albumName()

    //시작
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateAlbumBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        selectedFeed = ArrayList()
        newAlbumData = AlbumData() //앨범 데이터 객체 생성

        var feedList: ArrayList<Feed> =
            intent.getSerializableExtra("list") as ArrayList<Feed> //사용자가 작성한 피드의 목록을 보여줌

        Log.d("list",feedList.toString())
        //생성
        fm.beginTransaction().add(R.id.albumContainer, fragmentAlbumname, "0")
            .add(R.id.albumContainer, fragmentAlbumart, "1")
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
                    .hide(fragmentAlbumart)
                    .hide(fragmentSelectfeed)
                    .commitAllowingStateLoss()
            }
            1->{
                fm.beginTransaction()
                    .show(fragmentAlbumart)
                    .hide(fragmentAlbumname)
                    .hide(fragmentSelectfeed)
                    .commitAllowingStateLoss()
            }
            else->{
                fm.beginTransaction()
                    .show(fragmentSelectfeed)
                    .hide(fragmentAlbumname)
                    .hide(fragmentAlbumart)
                    .commitAllowingStateLoss()
            }
        }

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}