package com.example.bookworm.extension.follow.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.FragmentFollowListBinding
import com.example.bookworm.extension.follow.interfaces.Contract
import com.example.bookworm.extension.follow.modules.FollowItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

//팔로워, 팔로잉 탭의 틀을 가지고 있는 클래스 => 팔로워 탭과 팔로잉 탭의 구분은 isFollower변수로 체크한다.
//뷰는 가져온 데이터를 화면에 표시만 하는 역할을 한다
class FragmentFollowList(
        val token: String,
        val isfollower: Int,
) : Fragment(), Contract.View {
    var binding: FragmentFollowListBinding? = null
    private var followerAdapter: FollowItemAdapter? = null
    private lateinit var userList: ArrayList<UserInfo>
    private lateinit var fv: FollowViewModel

    //유저 뷰모델 정의
    private val userViewModel by lazy {
        ViewModelProvider(this, UserInfoViewModel.Factory(requireContext()))[UserInfoViewModel::class.java]
    }

    //Paging 처리를 위해서
    var page = 0
    var canLoad = true //더 불러올 수 있는 지
    var isLoading: Boolean = false
    var nowUser: UserInfo? = null //현재 사용자의 토큰
    var start: Boolean = true
    private val followerListLiveData = MutableLiveData<MutableList<UserInfo>>()
    val nowUserLiveData = MutableLiveData<UserInfo>()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        fv = context.let { FollowViewModel(it!!) }
        binding = FragmentFollowListBinding.inflate(layoutInflater)

        userViewModel.getUser(token, liveData = nowUserLiveData, true)

        followerListLiveData.observe(viewLifecycleOwner, ({
            showInfo(it)
        }))
        return binding!!.root
    }


    override fun onResume() {
        nowUserLiveData.observe(viewLifecycleOwner, ({
            nowUser = it
            loadUserData(start)
            if (start) start = false
            Log.d("데이터 보여줌", "ㅇㅇ")
        }))
        super.onResume()
    }

    //프레그먼트 종료시 메모리에서 바인딩을 해제
    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    //초기화
    fun init() {
        initValues()
        initAdapter()
    }

    fun initValues() {
        page = 1;isLoading = false; canLoad = true
        userList = ArrayList()
    }

    fun loadUserData(flag: Boolean) {
        if (flag) init()
        fv.getFollowList(followerListLiveData, isfollower != 0, token, false)
        if (flag) showShimmer(true)
    }

    //어댑터 초기화
    private fun initAdapter() {

        followerAdapter = context?.let {
            FollowItemAdapter(
                    it, nowUser as UserInfo, isfollower
            )
        }
        binding!!.recyclerView.adapter = followerAdapter
        binding!!.recyclerView.layoutManager = LinearLayoutManager(context)
        initScrollListener()
    }

    //리사이클러뷰 스크롤 초기화
    private fun initScrollListener() {
        binding!!.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastVisibleItemPosition =
                        layoutManager!!.findLastCompletelyVisibleItemPosition()
                if (!isLoading) {
                    try {
                        if (lastVisibleItemPosition == followerAdapter!!.itemCount - 1) {
                            deleteLoading()
                            //다음 데이터를 조회한다.
                            fv.getFollowList(followerListLiveData, isfollower != 0, token, false)
                            //현재 로딩을 끝냄을 알린다.
                            isLoading = true
                        }
                    } catch (e: NullPointerException) {
                    }
                }
            }
        })
    }

    // 로딩이 완료되면 프로그레스바를 지움
    fun deleteLoading() {
        userList.removeAt(userList.size - 1)
        followerAdapter!!.submitList(userList.toList())
    }


    //데이터를 세팅
    override fun showInfo(info: MutableList<UserInfo>?) {
        //팔로워가 없는 경우
        if (info == null) {
            canLoad = false
        } //더이상 로드하지 않음
        //가져온 데이터를 새롭게 담는다
        else {
            if (userList != info) userList.addAll(info)
            else if (info.size == 0)
            else {
                userList.clear()
                userList.addAll(info)
            }
            //만약 가져온 데이터가 최대치보다 작다면 => 더 가져올 데이터가 없음을 의미
            if (info.size < 10) canLoad = false
        }
        if (!canLoad) {
            isLoading = true
            followerAdapter!!.submitList(userList.toMutableList())

        } else {
            isLoading = false
            userList.add(UserInfo())
            followerAdapter!!.submitList(userList.toMutableList())
            page++ //로딩을 다하면 그 다음 페이지로 넘어간다
        }
        showShimmer(false)
    }

    //shimmer을 켜고 끄고 하는 메소드
    private fun showShimmer(bool: Boolean) {
        if (bool) {
            binding!!.llFollow.setVisibility(View.GONE)
            binding!!.SFLFollower.startShimmer()
            binding!!.SFLFollower.setVisibility(View.VISIBLE)
        } else {
            binding!!.llFollow.setVisibility(View.VISIBLE)
            binding!!.SFLFollower.stopShimmer()
            binding!!.SFLFollower.setVisibility(View.GONE)
        }
    }

}