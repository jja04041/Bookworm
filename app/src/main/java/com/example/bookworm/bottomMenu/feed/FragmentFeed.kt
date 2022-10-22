package com.example.bookworm.bottomMenu.feed

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.DdayCounter
import com.example.bookworm.LoadState
import com.example.bookworm.R
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.comments.SubActivityComment
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.databinding.FragmentFeedTopbarBinding
import com.example.bookworm.databinding.TmpActivityFeedBinding
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.RecyclerView.OnScrollListener as OnScrollListener


class FragmentFeed : Fragment() {
    private lateinit var binding: TmpActivityFeedBinding //Binding

    private val feedViewModel by lazy {
        ViewModelProvider(this, FeedViewModel.Factory(requireContext()))[FeedViewModel::class.java]
    }
    val feedAdapter by lazy {
        FeedAdapter()
    }
    private var isDataEnd = false // 파이어베이스 내에서 검색할 때, 데이터 끝인지 판별하는 변수
    private var need2Mov = -1
    private var storiesBar: RecyclerView? = null


    //다른 액티비티로부터의 결과값을 받기 위함
    var startActivityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == SubActivityCreatePost.CREATE_OK) {
            val item = result.data!!.getParcelableExtra<Feed>("feedData")
            need2Mov = 0 //상단으로 스크롤 하기 위한 플래그 설정
            feedAdapter.currentList.toList().apply {
                feedAdapter.submitList(listOf(item) + this)
            }
            Toast.makeText(context,
                    "게시물이 업로드 되었습니다.", Toast.LENGTH_SHORT)
                    .show()

        }
        /**
         * 게시물이 수정된 경우
         * */
        if (result.resultCode == SubActivityModifyPost.MODIFY_OK || result.resultCode == SubActivityComment.FEED_MODIFIED) {
            //수정된 아이템
            result.data!!.apply {
                //게시물 업로드 하는 함수
                val item = getParcelableExtra<Feed>("modifiedFeed")!!
                feedViewModel.uploadFeed(item, null, ImageProcessing(requireContext()), item.imgurl)
                feedViewModel.nowFeedUploadState.observe(viewLifecycleOwner, Observer {
                    if (it == LoadState.Done) {
                        //수정 업데이트 적용
                        need2Mov = item.position
                        item.duration = DdayCounter.getDuration(item.date!!)
                        feedAdapter.currentList.toMutableList().apply {
                            this.removeAt(need2Mov)
                            add(need2Mov, item)
                            feedAdapter.submitList(this.toList())
                        }
                        Toast.makeText(requireContext(), "게시물이 정상적으로 수정되었습니다. ", Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }
        //댓글 화면에서 게시물을 삭제하려고 한 경우
        if (result.resultCode == SubActivityComment.FEED_DELETE) {
            result.data!!.apply {
                val target = getParcelableExtra<Feed>("deleteTarget")!!
                feedAdapter.currentList.toMutableList().apply {
                    this.removeAt(target.position)
                    feedAdapter.submitList(this.toList())
                }
                Toast.makeText(context, "게시물이 정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = TmpActivityFeedBinding.inflate(layoutInflater)
        getFeeds(true) // 서버로부터 피드 정보를 받아옴

        //피드 새로 만들기
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            val data = ViewModelProvider(context as MainActivity, UserInfoViewModel.Factory(requireContext()))[UserInfoViewModel::class.java]
            val mainUser = data.suspendGetUser(null)
            FragmentFeedTopbarBinding.bind(binding.root).apply {
                imgCreatefeed.setOnClickListener {
                    val intent = Intent(context, SubActivityCreatePost::class.java)
                    intent.putExtra("mainUser", mainUser)
                    startActivityResult.launch(intent)
                }
                tvLogo.setOnClickListener {
                    binding.recyclerView.smoothScrollToPosition(0)
                }
            }

        }

        setAdapter()


        return binding.root
    }

    private fun check2Top() {
        //게시물 작성 완료 시 or 수정 완료시
        if (need2Mov >= 0) {
            binding.recyclerView.scrollToPosition(need2Mov)
            need2Mov = -1 //기본값으로 초기화
        }
    }

    private fun processFeedDelete(pos: Int) {
        val mutableList = feedAdapter.currentList.toMutableList()
        mutableList.removeAt(pos)
        if (!isDataEnd) {
            val liveData = MutableLiveData<Feed>()
            feedViewModel.loadPost(liveData)
            liveData.observe(viewLifecycleOwner, Observer { addedItem ->
                mutableList.removeLast()
                mutableList += listOf(addedItem, Feed())
                feedAdapter.submitList(mutableList)
            })
        } else
            feedAdapter.submitList(mutableList)

    }

    private fun setAdapter() {
        binding.apply {
            recyclerView.adapter = feedAdapter //어댑터 세팅
            //데이터 변경시 감지 후 자동 스크롤
            feedAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    check2Top()
                }

                override fun onChanged() {
                    check2Top()
                }
            })
            feedAdapter.setFeedMenuListener(object : OnFeedMenuClickListener {
                override fun onItemClick(holder: FeedViewHolder, view: View, position: Int) {
                    val feed = feedAdapter.currentList[position]
                    feed.position = position
                    val popupMenu = customMenuPopup(context!!, view)
                    if (feed.isUserPost) {
                        popupMenu.setItem(feed)
                        popupMenu.liveState.observe(viewLifecycleOwner, Observer { data ->
                            if (data == popupMenu.FEED_DELETE) {
                                //게시물 삭제시 새로운 게시물 하나를 더 불러옴.
                                processFeedDelete(position)
                            }
                        })
                    }
                }
            })

//            recyclerView.itemAnimator = null //리사이클러뷰 애니메이션 제거
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.setHasFixedSize(true)



            swiperefresh.setOnRefreshListener {
                pageRefresh()
            } //스와이프하여 새로고침
            with(recyclerView) {
                addOnScrollListener(object : OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        val lastVisibleItemPosition =
                                layoutManager!!.findLastCompletelyVisibleItemPosition()
                        if ((lastVisibleItemPosition
                                        == feedAdapter.currentList.lastIndex) && lastVisibleItemPosition > 0)
                            getFeeds(false)
                    }
                })
            }
        }
    }

    //화면 이동 시 보여졌던 키보드를 숨김
    override fun onHiddenChanged(hidden: Boolean) {
        val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (binding.root.findViewById<View?>(R.id.edtComment) != null) imm.hideSoftInputFromWindow(
                binding.root.findViewById<View>(R.id.edtComment).windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
        )
        super.onHiddenChanged(hidden)
    }


    //페이지 새로고침 시 사용하는 메소드
    fun pageRefresh() {
        showShimmer(true)
        isDataEnd = false
        getFeeds(true)
        feedAdapter.submitList(emptyList())
    }


    //FireStore에서 피드 가져옴
    private fun getFeeds(refreshing: Boolean) {
        if (!isDataEnd) {
            feedViewModel.loadPosts(refreshing)
            feedViewModel.nowFeedLoadState.observe(viewLifecycleOwner, Observer { nowState ->
                //피드를 새로 불러올 때 활성화
                showShimmer(
                        if (!refreshing) false
                        else nowState == LoadState.Loading)
                //데이터 로딩이 다 되었다면
                if (nowState == LoadState.Done) {
                    var current = feedAdapter.currentList.toMutableList() //기존에 가지고 있던 아이템 목록
                    if (refreshing) {
                        current.clear()
                        binding.swiperefresh.isRefreshing = false //새로고침인 경우, 데이터가 다 로딩 된 후 새로고침 표시 없애기
                    }
                    //만약 현재 목록이 비어있지 않고, 마지막 아이템이 로딩 아이템 이라면 마지막 아이템을 제거
                    if (current.isNotEmpty() && current.last().feedID == null) current.removeLast()
                    //데이터의 끝에 다다르지 않았다면, 현재 목록에 불러온 아이템을 추가한다.
                    if (feedViewModel.postsData != null && !current.containsAll(feedViewModel.postsData!!)) {
                        val resultData = feedViewModel.postsData!!.toMutableList()
                        val i = resultData.iterator()
                        //반복하면서 중복되는 데이터가 있는 경우 삭제
                        while (i.hasNext()) {
                            if (current.contains(i.next())) i.remove()
                        }
                        current.addAll(resultData)
                        current.add(Feed())
                    }
                    //데이터의 끝에 다다랐다면 끝이라는 것을 변수에 저장
                    else isDataEnd = true

                    //변경된 리스트를 어댑터에 반영
                    feedAdapter.submitList(current)
                }
            })
        }
    }


    //shimmer을 켜고 끄고 하는 메소드
    fun showShimmer(bool: Boolean) {
        binding.llFeed.isVisible = !bool
        if (bool) binding.SFLFeed.startShimmer() else binding.SFLFeed.stopShimmer()
        binding.SFLFeed.isVisible = bool
    }
}