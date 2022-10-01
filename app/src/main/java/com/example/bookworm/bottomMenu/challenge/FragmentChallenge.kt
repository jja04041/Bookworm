package com.example.bookworm.bottomMenu.challenge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.LoadState
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.example.bookworm.bottomMenu.challenge.subactivity.SubActivityCreateChallenge
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.item.AlbumProcessViewModel
import com.example.bookworm.databinding.FragmentChallengeBinding

/** 챌린지 탐색 및 참여가 가능한 프래그먼트
 * */

class FragmentChallenge : Fragment() {

    /** 데이터 연산을 위한 변수
     * */
    private val binding by lazy {
        FragmentChallengeBinding.inflate(layoutInflater)
    }
    private val challengeAdapter by lazy {
        ChallengeAdapter(requireContext())
    }
    private val challengeViewModel by lazy {
        ViewModelProvider(this, ChallengeViewModel.Factory(requireContext()))[ChallengeViewModel::class.java]
    }
    private val imm by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    }
    private var isDataEnd = false //데이터의 끝을 알려주는 flag


    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        //챌린지 생성 완료 시
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data = result.data!!.getParcelableExtra<Challenge>("challengeData")!!

        }
    }

    /** 실제 뷰를 그리는 함수
     * */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setAdapter()
        setUI()
        return binding.root
    }

    /** UI를 세팅하는 함수
     * */
    private fun setUI() {
        loadData() //서버로부터 챌린지 데이터를 받아온다.

        // 챌린지 생성 시도
        binding.apply {
            btnCreateChallenge.setOnClickListener {
                val intent = Intent(context, SubActivityCreateChallenge::class.java)
                startActivity(intent)
            }
            swiperefresh.setOnRefreshListener {
                pageRefresh()
            }
        }

    }


    /**페이지 새로고침시 사용되는 메소드*/
    fun pageRefresh() {
        showShimmer(true)
        isDataEnd = false
        loadData()
        challengeAdapter.submitList(emptyList())
    }

    /**챌린지 데이터를 불러오는 메소드*/
    private fun loadData(isRefreshing: Boolean = true) {
        val stateLiveData = MutableLiveData<LoadState>()
        val resultList = arrayListOf<Challenge>()
        if (!isDataEnd) {
            challengeViewModel.getChallengeList(isRefreshing = isRefreshing, stateLiveData = stateLiveData, result = resultList)
            stateLiveData.observe(viewLifecycleOwner) { state ->
                showShimmer(
                        if (!isRefreshing) false
                        else state == LoadState.Loading)
                when (state) {
                    LoadState.Done -> {
                        val current = challengeAdapter.currentList.toMutableList()
                        if (isRefreshing) {
                            current.clear()
                            binding.swiperefresh.isRefreshing = false
                        }
                        //로딩중 표시를 지움
                        if (current.isNotEmpty() && current.last().id == "") current.removeLast()
                        if (resultList.isNotEmpty() && !current.containsAll(resultList)) {
                            var i = resultList.iterator()
                            //반복하면서 중복되는 데이터가 있는 경우 삭제
                            while (i.hasNext()) {
                                if (current.contains(i.next())) i.remove()
                            }
                            current.addAll(resultList)
                            current.add(Challenge())

                        }

                        /**데이터 끝에 다다랐다면 끝이라는 것을 변수에 저장*/
                        else isDataEnd = true

                        /** 변경된 리스트를 어댑터에 반영
                         * */

                        challengeAdapter.submitList(current)
                    }
                    else -> {

                    }
                }
            }
        }
    }

    /**화면 이동 시 보여졌던 키보드를 숨김*/
    override fun onHiddenChanged(hidden: Boolean) {
        if (binding.root.findViewById<View?>(R.id.edtComment) != null) imm.hideSoftInputFromWindow(
                binding.root.findViewById<View>(R.id.edtComment).windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
        )
        super.onHiddenChanged(hidden)
    }

    /**어댑터 설정*/
    private fun setAdapter() {
        binding.mRecyclerView.apply {
            adapter = challengeAdapter
            itemAnimator = null //리사이클러뷰 애니메이션 제거
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    val lastVisibleItemPosition =
                            layoutManager!!.findLastCompletelyVisibleItemPosition()
                    //만약 화면끝에 도달한 경우, 데이터 로드 시도
                    if ((lastVisibleItemPosition == challengeAdapter.currentList.lastIndex) && lastVisibleItemPosition > 0)
                        loadData(false)
                }
            })
        }
    }

    //shimmer을 켜고 끄고 하는 메소드
    fun showShimmer(bool: Boolean) {
        binding.llChallenge.isVisible = !bool
        if (bool) binding.SFLChallenge.startShimmer() else binding.SFLChallenge.stopShimmer()
        binding.SFLChallenge.isVisible = bool
    }
}