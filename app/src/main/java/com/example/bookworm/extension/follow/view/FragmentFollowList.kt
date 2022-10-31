package com.example.bookworm.extension.follow.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.LoadState
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.FragmentFollowListBinding
import com.example.bookworm.extension.follow.interfaces.OnFollowBtnClickListener
import com.example.bookworm.extension.follow.modules.FollowItemAdapter

//팔로워, 팔로잉 탭의 틀을 가지고 있는 클래스 => 팔로워 탭과 팔로잉 탭의 구분은 isFollower변수로 체크한다.
//뷰는 가져온 데이터를 화면에 표시만 하는 역할을 한다
class FragmentFollowList(
    private val targetUserData: UserInfo,
    private val nowUserData: UserInfo,
    val isFollower: Int,
) : Fragment() {
    var binding: FragmentFollowListBinding? = null
    private val userInfoViewModel by lazy {
        ViewModelProvider(
            this,
            UserInfoViewModel.Factory(requireContext())
        )[UserInfoViewModel::class.java]
    }
    private var followerAdapter: FollowItemAdapter? = null
    private val followViewModel by lazy {
        ViewModelProvider(
            this,
            FollowViewModel.Factory(requireContext())
        )[FollowViewModel::class.java]
    }
    var canLoad = true //더 불러올 수 있는 지
    var isLoading: Boolean = false
    var start: Boolean = true
    private var resultList = emptyList<UserInfo>().toMutableList()
    private val loadStateLiveData = MutableLiveData<LoadState>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFollowListBinding.inflate(layoutInflater)
        //전달 받은 사용자 정보를 가져온다.
        loadStateLiveData.observe(viewLifecycleOwner, ({
            if (it == LoadState.Done) {
                showInfo(resultList)
                showShimmer(false)
            } else if (it == LoadState.Error) {
                showShimmer(false)
            }
        }))

        return binding!!.root
    }


    /**
     * 다시 화면을 돌아갔을 때 진행되는 메소드
     */
    override fun onResume() {
        loadUserData(start)
        //이미 시작한경우 , false 로 start 값을 세팅한다.
        if (start) start = false
        super.onResume()
    }

    // 프레그먼트 종료시 메모리에서 바인딩을 해제
    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    // 초기화
    fun init() {
        initValue()
        initAdapter()
    }

    //
    private fun initValue() {
        isLoading = false; canLoad = true
    }

    private fun loadUserData(flag: Boolean) {
        if (flag) {
            init()
            resultList.clear()
            followViewModel.getFollowList(
                stateLiveData = loadStateLiveData,
                isFollower == 0,
                targetUserData.token,
                false,
                resultList
            )
            showShimmer(true)
        }
    }

    //어댑터 초기화
    private fun initAdapter() {
        followerAdapter = context?.let {
            FollowItemAdapter(
                it, nowUserData
            )
        }
        followerAdapter!!.addListener(object : OnFollowBtnClickListener {
            override fun onItemClick(holder: FollowerViewHolder, v: View) {
                val liveData = MutableLiveData<UserInfo>()
                userInfoViewModel.getUser(null, liveData = liveData, true)
                liveData.observe(viewLifecycleOwner) {
                    if (it != null) {
                        //팔로잉 탭의 숫자를 변경
                        (activity as FollowerActivity).binding.tabLayout.getTabAt(1)!!.text =
                            "${it.followingCounts} 팔로잉"
                    }
                }
            }
        })
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
                        if ((lastVisibleItemPosition
                                    == followerAdapter!!.currentList.lastIndex - 1) && lastVisibleItemPosition > 0 && canLoad
                        ) {
                            resultList.clear()
                            //다음 데이터를 조회한다.
                            followViewModel.getFollowList(
                                loadStateLiveData,
                                isFollower == 0,
                                targetUserData.token,
                                false,
                                resultList
                            )
                            //현재 로딩을 끝냄을 알린다.
                            isLoading = true
                        }
                    } catch (e: NullPointerException) {
                    }
                }
            }
        })
    }

    //데이터를 세팅
    fun showInfo(result: MutableList<UserInfo>?) {
        val current = followerAdapter!!.currentList.toMutableList() // 기존에 가지고 있던 아이템 목록
        if (current.isEmpty()) {
            canLoad = false
        }
        //만약 현재 목록이 비어있지 않고 마지막 아이템이 로딩 아이템이라면 마지막 아이템을 제거
        if (current.isNotEmpty() && current.last().token == "") current.removeLast()
        if (result!!.size < 10) canLoad = false
        if (!current.containsAll(result)) {
            current.addAll(result)
            current.add(UserInfo())
        } else canLoad = false

        //변경된 리스트를 어댑터에 반영
        followerAdapter!!.submitList(result)
        showShimmer(false)

    }

    //shimmer을 켜고 끄고 하는 메소드
    private fun showShimmer(bool: Boolean) {
        binding!!.llFollow.isVisible = !bool
        binding!!.SFLFollower.apply {
            isVisible = bool
            if (bool) startShimmer()
            else stopShimmer()
        }
    }

}