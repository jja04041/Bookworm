package com.example.bookworm.Extension.Follow.View

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.Extension.Follow.Interfaces.Contract
import com.example.bookworm.Core.UserData.UserInfo
import com.example.bookworm.Core.UserData.PersonalD
import com.example.bookworm.Extension.Follow.Interfaces.PagerInterface
import com.example.bookworm.Extension.Follow.LoadData
import com.example.bookworm.Extension.Follow.Modules.FollowItemAdapter
import com.example.bookworm.databinding.FragmentFollowListBinding

//팔로워, 팔로잉 탭의 틀을 가지고 있는 클래스 => 팔로워 탭과 팔로잉 탭의 구분은 isFollower변수로 체크한다.
//뷰는 가져온 데이터를 화면에 표시만 하는 역할을 한다
class FragmentFollowList(val token: String, val isfollower: Int,val pager:PagerInterface.PageAdapter) : Fragment(), Contract.View {
    var binding: FragmentFollowListBinding? = null
    private var followerAdapter: FollowItemAdapter? = null
    private lateinit var userList: ArrayList<UserInfo>

    //Paging 처리를 위해서
    var page = 0
    var canLoad = true //더 불러올 수 있는 지
    var isLoading: Boolean = false
    var loadData: LoadData? = null
    var nowUser: UserInfo? = null //현재 사용자의 토큰

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        nowUser = PersonalD(context).userInfo as UserInfo
        loadData = LoadData(this, isfollower, nowUser as UserInfo) //값을 가져오는 모듈 초기화
        init();
        return binding!!.root
    }


    override fun onResume() {
        initValues()
        loadData!!.getData(token) //초기 데이터를 불러옴
        Log.d("데이터 보여줌","ㅇㅇ")
        super.onResume()
    }
    //프레그먼트 종료시 메모리에서 바인딩을 해제
    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    //초기화
    fun init() {
        binding = FragmentFollowListBinding.inflate(layoutInflater)
        initValues()
        initAdapter()
    }
    fun initValues(){
        loadData!!.lastVisible=null
        page = 1;isLoading = false; canLoad = true
        userList = ArrayList()
    }

    //어댑터 초기화
    private fun initAdapter() {

        followerAdapter = context?.let { FollowItemAdapter(it, nowUser as UserInfo,isfollower,pager) }
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
                        if (layoutManager != null && lastVisibleItemPosition == followerAdapter!!.itemCount - 1) {
                            deleteLoading()
                            //다음 데이터를 조회한다.
                            loadData!!.getData(token)
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
        userList!!.removeAt(userList.size - 1)
        followerAdapter!!.submitList(userList.toList())
    }


    //데이터를 세팅
    override fun showInfo(info: ArrayList<UserInfo>?) {
        //팔로워가 없는 경우
        if (info == null) {
            canLoad = false
        } //더이상 로드하지 않음
        //가져온 데이터를 새롭게 담는다
        else {
            userList!!.addAll(info)
            //만약 가져온 데이터가 최대치보다 작다면 => 더 가져올 데이터가 없음을 의미
            if (info.size < loadData!!.LIMIT) canLoad = false
        }
        if (!canLoad) {
            isLoading = true
            followerAdapter!!.submitList(userList.toList())
        } else {
            isLoading = false
            userList!!.add(UserInfo())
            followerAdapter!!.submitList(userList.toList())
            page++ //로딩을 다하면 그 다음 페이지로 넘어간다
        }
    }



}