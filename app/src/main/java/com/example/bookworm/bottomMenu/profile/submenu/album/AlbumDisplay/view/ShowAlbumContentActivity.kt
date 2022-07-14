package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.item.ShowAlbumContentAdapter
import com.example.bookworm.databinding.SubactivityShowalbumcontentBinding


//앨범의 내용을 보여주는 액티비티 (스크롤을 통해 앨범에 있는 포스트를 목록으로 확인 가능)
class ShowAlbumContentActivity: AppCompatActivity(){
    var binding:SubactivityShowalbumcontentBinding?=null
    var contentAdapter:ShowAlbumContentAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= SubactivityShowalbumcontentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        var data = intent.getSerializableExtra("albumData") as AlbumData //onclick 이벤트를 통해 넘겨받은 앨범 데이터
        setUI(data)
    }

    fun setUI(data:AlbumData){
        binding!!.edtTitle.setText(data.albumName) //앨범 명 설정
        Glide.with(this).load(data.thumbnail).into(binding!!.ivAlbumPic)//앨범 이미지 보이기
        //여기에는 앨범에 대한 설명이 있는 경우 보이도록
        binding!!.btnBack.visibility= View.VISIBLE //뒤로가기 버튼 보이게
        binding!!.btnBack.setOnClickListener({
            finish()
        })


        binding!!.btnMore.visibility=View.VISIBLE //메뉴 버튼 보이게
        binding!!.tvPostCnt.text="${data.containsList.size} 게시물"
        initRecyclerView(data)

    }
    fun initRecyclerView(data: AlbumData){
        contentAdapter= ShowAlbumContentAdapter(this)
        contentAdapter!!.submitList(data.containsList.toList())
        binding!!.mRecyclerView.adapter=contentAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding!!.mRecyclerView.layoutManager= linearLayoutManager
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null //메모리에서 해제
    }
}