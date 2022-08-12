package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.item.AlbumProcessViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.item.ShowAlbumContentAdapter
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.databinding.SubactivityCreatealbumcontentBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//새롭게 사용할 액티비티(앨범 생성 시)
class CreateAlbumContentActivity : AppCompatActivity() {
    var binding: SubactivityCreatealbumcontentBinding? = null
    lateinit var uv: UserInfoViewModel
    lateinit var albumProcessViewModel: AlbumProcessViewModel
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    lateinit var imageProcessing: ImageProcessing
    var contentAdapter: ShowAlbumContentAdapter? = null
    var albumData: AlbumData? = null
    var mode = 0 //모드 0: 생성모드 , 모드 1: 편집 모드
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initialize
        binding = SubactivityCreatealbumcontentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        collapsingToolbarLayout = binding!!.mCollapsingToolbar
        imageProcessing = ImageProcessing(this) //이미지 처리를 위한 모듈 생성
        uv = ViewModelProvider(this, UserInfoViewModel.Factory(this)).get(
            UserInfoViewModel::class.java
        )
        //앨범업로드과정을 담당하는 뷰모델
        albumProcessViewModel =
            ViewModelProvider(this, AlbumProcessViewModel.Factory(this, uv)).get(
                AlbumProcessViewModel::class.java
            )
        mode = if (intent.hasExtra("albumData")) 1 else 0
        uv.getUser(null, false)

        albumData =
            if (mode == 1) intent.getSerializableExtra("albumData") as AlbumData //편집 모드인 경우
            else AlbumData() //앨범 생성 모드인 경우
        setUI(mode)
    }

    fun setUI(mode: Int) {
        albumProcessViewModel.albumData = albumData!!

        //사용자 프로필 가져오기
        uv.data.observe(this, {
            albumData!!.creater = it.token
            Glide.with(this).load(it.profileimg).circleCrop().into(binding!!.ivUserProfilePic)
            binding!!.tvUserName.text = it.username
        })
        when (mode) {
            //생성모드
            0 -> processCreate()
            //편집모드
            else -> processModify()
        }

        //공통 리스너 부착
        //앨범 생성 / 수정 후 업로드 시
        binding!!.btnCreate.setOnClickListener({
            //앨범명에 오류가 없을 경우에만 업로드가 가능하도록 함.
            CoroutineScope(Dispatchers.Main).launch {
                if (albumProcessViewModel.titleDuplicationCheck(binding!!.edtTitle.text.toString()) && binding!!.ivAlbumPic.resources != null) {
                    albumProcessViewModel.modifyName(binding!!.edtTitle.text.toString())
                    albumProcessViewModel.uploadAlbum()
                }
            }
        })
        //이미지 수정 시
        binding!!.ivAlbumPic.setOnClickListener({
            imageProcessing.initProcess()
            imageProcessing.bitmapUri.observe(this, {
                albumProcessViewModel.modifyThumb(it.toString())
                Glide.with(binding!!.root).load(it).into(binding!!.ivAlbumPic)
            })
        })
        //이전 액티비티로 돌아가기
        binding!!.btnCancel.setOnClickListener({
            finish()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    //생성시 작동하는 Function
    fun processCreate() {
        //버튼의 텍스트 변경
        binding!!.btnCreate.text = "생성"
        binding!!.btnCancel.text = "취소"
        //생성 시에만 사용하는 기능들

    }

    //편집시 작동하는 Function
    fun processModify() {
        //버튼의 텍스트 변경
        binding!!.btnCreate.text = "적용"
        binding!!.btnCancel.text = "이전"
    }

    //Scroll 제어
    fun enablescroll() {
        val params = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams

        params.setScrollFlags(
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        )
        collapsingToolbarLayout.layoutParams = params
        var isShow = true
        var scrollRange = -1
        binding!!.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset <= 0.5) {
                Log.d("offset", (scrollRange + verticalOffset).toString())
                collapsingToolbarLayout.title = binding!!.edtTitle.text.toString()
                isShow = true
            } else if (isShow) {
                collapsingToolbarLayout.title =
                    " " //careful there should a space between double quote otherwise it wont work
                isShow = false
            }
        })
    }

    fun disablescoll() {
        val params = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        collapsingToolbarLayout.layoutParams = params
    }

    //리사이클러뷰 초기화
    fun initRecyclerView(data: AlbumData?) {
        contentAdapter = ShowAlbumContentAdapter(this)
        contentAdapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                if (itemCount <= 2) disablescoll()
                else enablescroll()
            }
        })
        if (data != null) contentAdapter!!.submitList(data.containsList.toList())
        binding!!.mRecyclerView.adapter = contentAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding!!.mRecyclerView.layoutManager = linearLayoutManager
    }
}