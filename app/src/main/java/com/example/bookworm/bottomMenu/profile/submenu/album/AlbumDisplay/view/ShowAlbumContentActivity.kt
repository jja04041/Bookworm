package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.Feed.views.FeedViewModel
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.item.ShowAlbumContentAdapter
import com.example.bookworm.databinding.SubactivityShowalbumcontentBinding


//앨범의 내용을 보여주는 액티비티 (스크롤을 통해 앨범에 있는 포스트를 목록으로 확인 가능)
class ShowAlbumContentActivity : AppCompatActivity() {
    var binding: SubactivityShowalbumcontentBinding? = null
    var contentAdapter: ShowAlbumContentAdapter? = null
    var mode: Int = 0

    lateinit var uv: UserInfoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SubactivityShowalbumcontentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        uv = ViewModelProvider(this, UserInfoViewModel.Factory(this)).get(
            UserInfoViewModel::class.java
        )

        //보기 모드(mode = 0)
        if (intent.hasExtra("albumData")) {
            var data =
                intent.getSerializableExtra("albumData") as AlbumData //onclick 이벤트를 통해 넘겨받은 앨범 데이터

            if (data.creater != null)
                uv.getUser(data.creater, true) //앨범 생성자의 데이터를 가져옴
            setUI(data)
        }

        //편집 모드 (mode = 1)
        else {
            mode = 1
            var NewAlbumData = AlbumData() //앨범 객체 생성
            setUI(NewAlbumData)
        }
    }


    //UI 초기화
    fun setUI(data: AlbumData) {
        when (mode) {

            //편집 모드
            1 -> {
                setEditModBtnVis(true)


            }

            //기본값 (보기 모드)
            else -> {
                binding!!.edtTitle.setText(data.albumName) //앨범 명 설정
                binding!!.edtTitle.isEnabled = false //편집모드에서만 편집되도록 함.
                Glide.with(this).load(data.thumbnail).into(binding!!.ivAlbumPic)//앨범 이미지 보이기
                //여기에는 앨범에 대한 설명이 있는 경우 보이도록
                setEditModBtnVis(false)
                binding!!.tvPostCnt.text = "${data.containsList.size} 게시물"
                //사용자명 보이기
                uv.data.observe(this, { userinfo ->
                    Glide.with(binding!!.root).load(userinfo.profileimg).circleCrop()
                        .into(binding!!.ivUserProfilePic) //이미지 삽입
                    binding!!.tvUserName.setText(userinfo.username)
                })
                initRecyclerView(data)
            }
        }


    }

    //true인 경우 편집모드 전용 버튼 활성화, false인 경우 보기 모드 전용 버튼 활성화
    fun setEditModBtnVis(bool: Boolean) {
        var editShow = if (bool) View.VISIBLE else View.GONE
        var disShow = if (bool) View.GONE else View.VISIBLE


        //보기 모드 버튼
        binding!!.btnBack.visibility = disShow //뒤로가기 버튼
        binding!!.btnMore.visibility = disShow //메뉴 버튼
        binding!!.btnBack.setOnClickListener({
            finish()
        })

        //편집 모드 버튼
        binding!!.edtTitle.isEnabled = bool //
        binding!!.btnCancel.visibility = editShow //취소 버튼
        binding!!.btnCreate.visibility = editShow // 생성 버튼
    }

    //리사이클러뷰 초기화
    fun initRecyclerView(data: AlbumData) {
        contentAdapter = ShowAlbumContentAdapter(this)
        contentAdapter!!.submitList(data.containsList.toList())
        binding!!.mRecyclerView.adapter = contentAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding!!.mRecyclerView.layoutManager = linearLayoutManager
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null //메모리에서 해제
    }
}