package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.item.AlbumProcessAdapter
import com.example.bookworm.core.dataprocessing.image.ImagePicker
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.LayoutShowFeedlistForAlbumBinding

class ShowFeedListForCreateAlbum : AppCompatActivity() {
    val binding by lazy {
        LayoutShowFeedlistForAlbumBinding.inflate(layoutInflater)
    }

    private val albumFeedListAdapter by lazy { AlbumProcessAdapter(this) }
    val userInfoViewModel by lazy {
        ViewModelProvider(this, UserInfoViewModel.Factory(this)).get(
                UserInfoViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setAdapter()
        userInfoViewModel.getUser(null, MutableLiveData<UserInfo>().apply {
            observe(this@ShowFeedListForCreateAlbum) {
                loadData(it)
                setUI()
            }
        }, false)
    }

    var startResult = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        run {
            if (result.resultCode == RESULT_OK) {
                finish()
            }
        }
    }

    /** 어댑터 설정
     * */
    private fun setAdapter() {
        binding.mRecyclerView.apply {
            adapter = albumFeedListAdapter
            layoutManager = LinearLayoutManager(this@ShowFeedListForCreateAlbum)

        }
    }

    /** 사용자의 피드 리스트를 불러온다.
     * */
    private fun loadData(userInfo: UserInfo) {
        userInfoViewModel.getFeedList(userInfo.token)
        userInfoViewModel.feedList.observe(this) { feedlist ->
            albumFeedListAdapter.submitList(feedlist)
        }
    }

    private fun setUI() {
        binding.apply {
            //다음을 눌렀을 경우
            albumFeedListAdapter.selectedFeed.observe(this@ShowFeedListForCreateAlbum) { feedList ->
                tvNext.apply {
                    isEnabled = feedList.size > 1 //최소 두개 이상의 게시물을 선택해야 앨범을 만들 수 있음
                    setTextColor(if (feedList.size > 1) Color.GREEN else Color.GRAY)
                    setOnClickListener {
                        startResult.launch(Intent(
                                this@ShowFeedListForCreateAlbum, CreateAlbumContentActivity::class.java
                        ).apply { putParcelableArrayListExtra("selectedFeedList", feedList) })

                    }
                }
            }

            tvBack.apply {
                setTextColor(Color.parseColor("#FF7F50"))
                setOnClickListener {
                    finish()
                }
            }


        }
    }
}