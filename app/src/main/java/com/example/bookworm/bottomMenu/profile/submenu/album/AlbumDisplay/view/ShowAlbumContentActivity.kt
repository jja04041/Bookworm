package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.view

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.item.ShowAlbumContentAdapter
import com.example.bookworm.databinding.SubactivityShowalbumcontentBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.coroutines.*


//앨범의 내용을 보여주는 액티비티 (스크롤을 통해 앨범에 있는 포스트를 목록으로 확인 가능)
class ShowAlbumContentActivity : AppCompatActivity() {
    var binding: SubactivityShowalbumcontentBinding? = null
    var contentAdapter: ShowAlbumContentAdapter? = null
    lateinit var toolbar: Toolbar
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    lateinit var albumOptionMenu: AlbumOptionMenu
    lateinit var uv: UserInfoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        //initialize
        super.onCreate(savedInstanceState)
        binding = SubactivityShowalbumcontentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //Toolbar
        toolbar = binding!!.toolbar
        setSupportActionBar(toolbar)
        collapsingToolbarLayout = binding!!.mCollapsingToolbar

        uv = ViewModelProvider(this, UserInfoViewModel.Factory(this)).get(
            UserInfoViewModel::class.java
        )
        var data =
            intent.getSerializableExtra("albumData") as AlbumData //onclick 이벤트를 통해 넘겨받은 앨범 데이터

        if (data.creater != null)
            uv.getUser(data.creater, true) //앨범 생성자의 데이터를 가져옴
        //delay
        binding!!.root.delayOnLifeCycle(100L) {
            if (binding!!.mRecyclerView.adapter!!.itemCount > 1) enablescroll()
            else disablescoll()
        }

        setUI(data)

    }


    //UI 초기화
    fun setUI(data: AlbumData) {
        Glide.with(this).load(data.thumbnail).into(binding!!.ivAlbumPic)//앨범 이미지 보이기
        //여기에는 앨범에 대한 설명이 있는 경우 보이도록

        binding!!.tvPostCnt.text = "${data.containsList.size} 게시물"
        //사용자명 보이기
        uv.userInfoLiveData.observe(this, { userinfo ->
            Glide.with(binding!!.root).load(userinfo.profileimg).circleCrop()
                .into(binding!!.ivUserProfilePic) //이미지 삽입
            binding!!.tvUserName.setText(userinfo.username)
        })
        setToolbarTitleUI("안녕하세요 제이름은 김삼순입니다.")
//                setToolbarTitleUI(data.albumName!!)
        //옵션 메뉴
        binding!!.btnMore.setOnClickListener({
            albumOptionMenu = AlbumOptionMenu(this, it, data)
            albumOptionMenu.setOnMenuItemClickListener(albumOptionMenu)
            albumOptionMenu.show()
        })
        binding!!.appBarLayout.addOnOffsetChangedListener(object :
            AppBarStateChangeListener() {

            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                Log.d("STATE", state!!.name);
            }

        })
        initRecyclerView(data)

    }

    //toolbar의 디자인을 담당하는 함수
    fun setToolbarTitleUI(text: String) {
        var str = text
        str = if (str.length > 10) str.substring(0, 10) + "\n" + str.substring(10) else str
        if (str.length > 10) setMargin(binding!!.tvPostCnt, 0, 0, 0, 60) //margin을 주기
        collapsingToolbarLayout.setTitle(str)
    }

    fun setMargin(v: View, left: Int, top: Int, right: Int, bottom: Int) {
        var params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(dp2px(left), dp2px(top), dp2px(right), dp2px(bottom))
        v.layoutParams = params
    }

    fun dp2px(dp: Int): Int {
        val r: Resources = this.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.getDisplayMetrics()
        ).toInt()
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


    //function for delay(View)
    fun View.delayOnLifeCycle(
        durationInMillis: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        block: () -> Unit
    ):
            Job? = findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
        lifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
            delay(durationInMillis)
            block()
        }
    }

    fun enablescroll() {
        val params = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams


        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)

        params.setScrollFlags(
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        )
        collapsingToolbarLayout.layoutParams = params

    }

    fun disablescoll() {
        val params = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        collapsingToolbarLayout.layoutParams = params
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)

    }

    abstract class AppBarStateChangeListener : OnOffsetChangedListener {
        enum class State {
            EXPANDED, COLLAPSED, IDLE
        }

        private var mCurrentState = State.IDLE
        override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
            mCurrentState = if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                State.EXPANDED
            } else if (Math.abs(i) >= appBarLayout.totalScrollRange) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                State.COLLAPSED
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                State.IDLE
            }
        }

        abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: State?)
    }

}