package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.item.AlbumProcessViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.item.ShowAlbumContentAdapter
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.SubactivityCreatealbumcontentBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//새롭게 사용할 액티비티(앨범 생성 시)
class CreateAlbumContentActivity : AppCompatActivity() {
    var binding: SubactivityCreatealbumcontentBinding? = null
    val userInfoViewModel by lazy {
        ViewModelProvider(this, UserInfoViewModel.Factory(this)).get(
                UserInfoViewModel::class.java
        )
    }
    val albumProcessViewModel by lazy {
        ViewModelProvider(this, AlbumProcessViewModel.Factory(this, userInfoViewModel))[AlbumProcessViewModel::class.java]
    }
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    lateinit var imageProcessing: ImageProcessing

    private val userLiveData = MutableLiveData<UserInfo>()
    var contentAdapter: ShowAlbumContentAdapter? = null
    val album by lazy {
        //앨범 편집모드인 경우
        if (intent.hasExtra("albumData")) {
            intent.getParcelableExtra("albumData")!!
        }
        //앨범 생성 모드인 경우
        else AlbumData(
                selectedFeedList = intent.getParcelableArrayListExtra("selectedFeedList")!!)
    }
    val mode by lazy {
        intent.hasExtra("albumData")
    }
    lateinit var imm:InputMethodManager

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(currentFocus!=null){
            val rect = Rect()
            currentFocus!!.getGlobalVisibleRect(rect)
            if(!rect.contains(ev!!.x.toInt(),ev.y.toInt())){
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
                currentFocus!!.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initialize
        binding = SubactivityCreatealbumcontentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        imm= this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        collapsingToolbarLayout = binding!!.mCollapsingToolbar
        imageProcessing = ImageProcessing(this) //이미지 처리를 위한 모듈 생성
        userInfoViewModel.getUser(null, userLiveData, false)
        userLiveData.observe(this) { userInfo ->
            setUI(userInfo)
            initRecyclerView(album)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUI(nowUserInfo: UserInfo) {
        binding?.apply {
            //사용자 프로필 가져오기
            nowUserInfo.apply {
                album.creatorToken = token
                Glide.with(this@CreateAlbumContentActivity).load(profileimg).circleCrop().into(ivUserProfilePic)
                tvUserName.text = username
            }
            when (mode) {
                //생성모드
                false -> processCreate()
                //편집모드
                else -> processModify()
            }
            tvPostCnt.text = "${album.selectedFeedList.size} ${tvPostCnt.text}"
            //공통 리스너 부착
            //앨범 생성 / 수정 후 업로드 시
            btnCreate.setOnClickListener {
                //앨범명에 오류가 없을 경우에만 업로드가 가능하도록 함.
                CoroutineScope(Dispatchers.Main).launch {
                    if (albumProcessViewModel.titleDuplicationCheck(edtTitle.text.toString()) && ivAlbumPic.resources != null) {
                        album.albumName = edtTitle.text.toString()
                        albumProcessViewModel.uploadAlbum(album)
                    }
                }
            }


            //이미지 수정 시
            ivAlbumPic.setOnClickListener {
                imageProcessing.initProcess()
                imageProcessing.bitmapUri.observe(this@CreateAlbumContentActivity) {
                    album.thumbnail = it.toString()
                    Glide.with(root).load(it).into(ivAlbumPic)
                    tvAddCoverImg.isVisible = false
                }
            }
            //이전 액티비티로 돌아가기
            btnCancel.setOnClickListener {
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    //생성시 작동하는 Function
    private fun processCreate() {
        //버튼의 텍스트 변경
        binding!!.btnCreate.text = "생성"
        binding!!.btnCancel.text = "이전"
        //생성 시에만 사용하는 기능들

    }

    //편집시 작동하는 Function
    private fun processModify() {
        //버튼의 텍스트 변경
        binding!!.btnCreate.text = "적용"
        binding!!.btnCancel.text = "이전"
    }

    //Scroll 제어
    fun enableScroll() {
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

    fun disableScroll() {
        val params = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        collapsingToolbarLayout.layoutParams = params
    }

    //리사이클러뷰 초기화
    fun initRecyclerView(data: AlbumData?) {
        contentAdapter = ShowAlbumContentAdapter(this)
        contentAdapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                if (itemCount <= 2) disableScroll()
                else enableScroll()
            }
        })
        if (data != null) contentAdapter!!.submitList(data.selectedFeedList.toList())
        binding!!.mRecyclerView.adapter = contentAdapter
        binding!!.mRecyclerView.setOnClickListener{
            val windowHeightMethod = InputMethodManager::class.java.getMethod("getInputMethodWindowVisibleHeight")
            val height = windowHeightMethod.invoke(imm) as Int
            if(height > 0)  imm.hideSoftInputFromWindow(binding!!.edtTitle.windowToken, 0)
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding!!.mRecyclerView.layoutManager = linearLayoutManager
    }
}