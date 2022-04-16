package com.example.bookworm.Follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.Feed.Comments.DiffUtilCallback
import com.example.bookworm.User.UserInfo
import com.example.bookworm.databinding.FragmentProfileFollowerBinding
import com.example.bookworm.modules.personalD.PersonalD
import com.google.firebase.firestore.DocumentSnapshot

class fragment_profile_follower(val token:String) : Fragment(),Contract.View{
    var binding: FragmentProfileFollowerBinding? = null
    private var followerAdapter:FollowerAdapter?=null
    private var userList:ArrayList<UserInfo>?=null

    //Paging 처리를 위해서
    var page=0
    var canLoad=true //더 불러올 수 있는 지
    var isLoading: Boolean=false
    var loadData:LoadData?=null
    var nowUser:UserInfo?=null //현재 사용자의 토큰
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initValues()
        loadData!!.getData(token) //초기 데이터를 불러옴
        return binding!!.root
    }

    //초기화
    fun initValues(){
        page=1;isLoading=false; canLoad = true
        binding = FragmentProfileFollowerBinding.inflate(layoutInflater)
        userList= ArrayList()
        nowUser= PersonalD(context).userInfo as UserInfo
        loadData=LoadData(this,true,nowUser as UserInfo) //값을 가져오는 모듈 초기화
        initAdapter()
    }

    //액티비티 종료시
    override fun onDestroy() {
        super.onDestroy()
        binding=null //메모리에서 바인딩 해제
    }

    //아이템의 길이가 변경될 때 어답터에게 알림
    fun replaceItem(newthings: ArrayList<UserInfo>) {
        val callback = DiffUtilCallback(userList, newthings)
        val diffResult = DiffUtil.calculateDiff(callback, true)
        userList!!.clear()
        userList!!.addAll(newthings)
        followerAdapter!!.setData(userList)
        diffResult.dispatchUpdatesTo(followerAdapter!!)
    }

    //어댑터 초기화
    private fun initAdapter(){
        followerAdapter= context?.let { FollowerAdapter(userList, it,nowUser as UserInfo) }
        binding!!.recyclerView.adapter=followerAdapter
        binding!!.recyclerView.layoutManager=LinearLayoutManager(context)
        initScrollListener()
    }
    //리사이클러뷰 스크롤 초기화
    private fun initScrollListener() {
        binding!!.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastVisibleItemPosition = layoutManager!!.findLastCompletelyVisibleItemPosition()
                if (!isLoading) {
                    try {
                        if (layoutManager != null && lastVisibleItemPosition ==  followerAdapter!!.itemCount- 1) {
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
        val arr: ArrayList<UserInfo> = ArrayList(userList)
        arr.removeAt(arr.size - 1)
        replaceItem(arr) //데이터가 삭제됨을 알림.
    }


    //데이터를 세팅
    override fun showInfo(info: ArrayList<UserInfo>?) {
        var newList: ArrayList<UserInfo> = ArrayList()
        if (info == null) {
            //팔로워가 없는 경우
            canLoad = false //더이상 로드하지 않음
            if (page > 1) newList.addAll(userList!!)
        }
        //가져온 데이터를 새롭게 담는다
        else {
            newList.addAll(userList!!)
            newList.addAll(info) //가져온 데이터 담기
            if (info.size < loadData!!.LIMIT) canLoad = false
        }

        if (!canLoad) {
            isLoading = true
            replaceItem(newList)
        } else {
            isLoading = false
            newList.add(UserInfo()) //로딩바 표시를 위한 빈 값
            replaceItem(newList)
            page++ //로딩을 다하면 그 다음 페이지로 넘어간다
        }
    }
}