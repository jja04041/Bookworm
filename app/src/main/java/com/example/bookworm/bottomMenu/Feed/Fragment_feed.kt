@file:JvmName("fragment_feed")
package com.example.bookworm.bottomMenu.Feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.Feed.items.FeedAdapter
import com.example.bookworm.bottomMenu.Feed.items.Story
import com.example.bookworm.bottomMenu.Feed.views.StoryDeco
import com.example.bookworm.bottomMenu.Feed.views.StorybarAdapter
import com.example.bookworm.core.internet.FBModule
import com.example.bookworm.databinding.FragmentFeedBinding
import com.example.bookworm.databinding.FragmentFeedTopbarBinding
import com.google.firebase.firestore.DocumentSnapshot

class Fragment_feed : Fragment() {
    var binding: FragmentFeedBinding? = null

    lateinit var feedAdapter: FeedAdapter
    private val LIMIT = 3
    lateinit var feedList: ArrayList<Feed?>
    private var lastVisible //마지막에 가져온 값 부터 추가로 가져올 수 있도록 함.
            : DocumentSnapshot? = null
    private lateinit var map: HashMap<Any?, Any?>
    private var fbModule: FBModule? = null
    lateinit var mView:View
    //isLoading:스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
    //canLoad:더 로딩이 가능한지 확인하는 변수[자료의 끝을 판별한다.]
    private var isRefreshing = false
    private var isLoading = false
    private var canLoad = true
    private var page = 1

    private var storiesBar: RecyclerView? = null

    //static 변수로 사용할 변수들
    companion object {
        lateinit var mContext: Context
        //qb
        fun setContext(con: Context) {
            mContext =con
        }
    }

    var startActivityResult = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == subActivity_Feed_Create.CREATE_OK) {
            pageRefresh()
        }
        if (result.resultCode == subActivity_Feed_Modify.MODIFY_OK) {
            val item = result.data!!.getSerializableExtra("modifiedFeed") as Feed?
            feedList.set(item!!.position,item)
            feedAdapter.submitList(feedList.toList())
            binding!!.recyclerView.smoothScrollToPosition(item.position)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fbModule = FBModule(context)
        fbModule!!.setLIMIT(LIMIT) //한번에 보여줄 피드의 최대치를 설정
        binding = FragmentFeedBinding.inflate(layoutInflater)
        binding!!.recyclerView.isNestedScrollingEnabled = false

        showShimmer(true)//시머 on

        //Create New Feed
        FragmentFeedTopbarBinding.bind(binding!!.root).imgCreatefeed.setOnClickListener {
            val intent = Intent(context, subActivity_Feed_Create::class.java)
            startActivityResult.launch(intent)
        }
        mView=binding!!.root

        // 스토리바
//        storiesBar = mView.findViewById(R.id.storybar)
//
//        val stories: MutableList<Story> = java.util.ArrayList()
//        for (i in 0..9) stories.add(Story(false))
//
//        val adapter = StorybarAdapter(stories)
//        storiesBar
//        storiesBar!!.setAdapter(adapter)
//        storiesBar!!.setLayoutManager(LinearLayoutManager(context, RecyclerView.HORIZONTAL, false))
//        storiesBar!!.addItemDecoration(StoryDeco(10))

        //companion 객체에 context값을 적재함
        setContext(requireContext())
        //스와이프하여 새로고침
        binding!!.swiperefresh.setOnRefreshListener {
            pageRefresh()
            isRefreshing = true
        }
        binding!!.recyclerView.setItemViewCacheSize(3)
        //피드 초기 호출
        pageRefresh()


        // Inflate the layout for this fragment
        return mView
    }

    //화면 이동 시 보여졌던 키보드를 숨김
    override fun onHiddenChanged(hidden: Boolean) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (mView.findViewById<View?>(R.id.edtComment) != null) imm.hideSoftInputFromWindow(
            mView.findViewById<View>(R.id.edtComment).windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
        super.onHiddenChanged(hidden)
    }

    //뷰보다 프레그먼트의 생명주기가 길어서, 메모리 누수 발생가능
    //누수 방지를 위해 뷰가 Destroy될 때, binding값을 nullify 함.
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun initAdapter() {
        feedAdapter = FeedAdapter(requireContext())
        feedAdapter.setHasStableIds(true)
        binding!!.recyclerView.adapter = feedAdapter
        initScrollListener() //무한스크롤
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
                        if (layoutManager != null && lastVisibleItemPosition == feedAdapter!!.itemCount - 1) {
                            deleteLoading()
                            //이전에 가져왔던 자료를 인자로 보내주어 그 다음 자료부터 조회한다.
                            map!!["lastVisible"] = lastVisible!!
                            //쿼리를 보내어, 데이터를 조회한다.
                            fbModule!!.readData(1, map, null)
                            //현재 로딩을 끝냄을 알린다.
                            isLoading = true
                        }
                    } catch (e: NullPointerException) {
                    }
                }
            }
        })
    }

    //페이지 새로고침 시 사용하는 메소드
    private fun pageRefresh() {
        initFeed()
        map = HashMap()
        if (map.get("lastVisible") != null) map.remove("lastVisible")
        feedList = ArrayList() //챌린지를 담는 리스트 생성
        isLoading = true
        fbModule!!.readData(1, map, null)
        showShimmer(true)
    }

    //피드 초기화
    private fun initFeed() {
        isLoading = true
        page = 1
        canLoad = true
        lastVisible = null
        feedList = ArrayList()
        initAdapter()
    }

    // 로딩이 완료되면 프로그레스바를 지움
    fun deleteLoading() {
        feedList!!.removeAt(feedList!!.size - 1)
        feedAdapter!!.submitList(feedList.toList()) //데이터가 삭제됨을 알림.
    }


    //fb모듈을 통해 전달받은 값을 세팅
    fun moduleUpdated(a: List<DocumentSnapshot>?, b: ArrayList<DocumentSnapshot?>?) {
        if (isRefreshing) {
            binding!!.swiperefresh.isRefreshing = false
            isRefreshing = false
        }
        if (a == null) {
            if (page == 1) Toast.makeText(context, "화면에 표시할 포스트가 없습니다.", Toast.LENGTH_SHORT).show()
            canLoad = false
            isLoading = true
        } else {
            //가져온 데이터를 for문을 이용하여, feed리스트에 차곡차곡 담는다.
            for (i in a.indices) {
                val Adata: Map<*, *>? = a[i].data //피드 데이터
                val Bdata: Map<*, *>? = if (b?.get(i) == null) null else b[i]!!
                    .data //상단의 댓글 데이터
                val feed = Feed()
                feed.setData(Adata, Bdata)
                feedList!!.add(feed)
            }
            //가져온 값의 마지막 snapshot부터 이어서 가져올 수 있도록 하기 위함.
            lastVisible = a[a.size - 1]
            //리사이클러뷰에서 튕김현상이 발생하여 넣어준 코드
            //현재 불러오는 값의 크기(a.size())가 페이징 제한 값(LIMIT)보다 작은 경우 => 더이상 불러오지 않게 함.
            if (a.size < LIMIT) {
                canLoad = false
            }
        }
        //만약 더이상 불러오지 못 할 경우
        if (!canLoad) {
            isLoading = true
            feedAdapter.submitList(feedList.toList())
        } else {
            isLoading = false
            feedList.add(Feed()) //로딩바 표시를 위한 빈 값
            feedAdapter.submitList(feedList.toList())
            page++ //로딩을 다하면 그다음 페이지로 넘어간다.
        }
        showShimmer(false)
    }

    //shimmer을 켜고 끄고 하는 메소드
    private fun showShimmer(bool: Boolean) {
        if (bool) {
            binding!!.llFeed.setVisibility(View.GONE)
            binding!!.SFLFeed.startShimmer()
            binding!!.SFLFeed.setVisibility(View.VISIBLE)
        } else {
            binding!!.llFeed.setVisibility(View.VISIBLE)
            binding!!.SFLFeed.stopShimmer()
            binding!!.SFLFeed.setVisibility(View.GONE)
        }
    }
}