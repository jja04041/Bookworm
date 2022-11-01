package com.example.bookworm.bottomMenu.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.LoadState
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.profile.views.ProfileInfoActivity
import com.example.bookworm.bottomMenu.search.bookitems.Book
import com.example.bookworm.bottomMenu.search.bookitems.BookAdapter
import com.example.bookworm.bottomMenu.search.bookitems.OnBookItemClickListener
import com.example.bookworm.bottomMenu.search.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.views.BookDetailActivity
import com.example.bookworm.bottomMenu.search.views.SearchMainActivity
import com.example.bookworm.databinding.FragmentSearchBinding


class FragmentSearch : Fragment() {
    private val binding by lazy {
        FragmentSearchBinding.inflate(layoutInflater)
    }

    //뷰모델에 데이터 가공과 처리를 위임한다.
    // -> 프레그먼트에서는 단순히 UI 세팅만 담당하도록 하여 부담을 줄인다.
    private val searchViewModel by lazy {
        ViewModelProvider(activity as MainActivity,
                SearchViewModel.Factory(context as MainActivity)
        )[SearchViewModel::class.java]
    }
    private val bookAdapter by lazy {
        BookAdapter(requireContext())
    }

    var rankFB: RankFB? = null

    var nickNameListGlobal: Array<TextView>? = null
    var readCountListGlobal: Array<TextView>? = null
    var profileListGlobal: Array<ImageView>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding.apply {
            edtSearchBtn.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    val intent = Intent(activity, SearchMainActivity::class.java)
                    startActivity(intent)
                    binding.edtSearchBtn.clearFocus()
                }
            }

            showShimmer(true)

            rankFB = RankFB(requireContext());
            rankFB!!.getRanking()

            var nickNameList = arrayOf(tv1stNickname, tv2ndNickname, tv3rdNickname)
            var readCountList = arrayOf(tv1stReadCount, tv2ndReadCount, tv3rdReadCount)
            var profileList = arrayOf(img1stProfile, img2ndProfile, img3rdProfile)

            nickNameListGlobal = nickNameList
            readCountListGlobal = readCountList
            profileListGlobal = profileList

        }
        setAdapter()
        loadRecommendBooks();

        return binding.root
    }


    fun setRanking(readCount: String, index: Int) {
        readCountListGlobal!![index].setText(readCount + '권')
    }

    fun setRanking(map: Map<String, Any>, index: Int) {
        nickNameListGlobal!![index].text = map.get("username").toString()
        Glide.with(requireContext()).load(map["profileimg"].toString()).circleCrop().into(profileListGlobal!![index])

        //프로필 클릭 시 해당 사용자의 프로필 정보 화면으로 이동하게
        profileListGlobal!![index].setOnClickListener{
            val intent = Intent(requireContext(), ProfileInfoActivity::class.java)
            intent.putExtra("userID", map["token"].toString())
            requireContext().startActivity(intent)
        }

        showShimmer(false)
    }


    private fun setAdapter(){
        //인기도서 onclick
        bookAdapter.addListener(object : OnBookItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, view: View, position: Int) {
                if (holder is BookAdapter.RecomBookViewHolder) {
                    val intent = Intent(requireContext(), BookDetailActivity::class.java)
                    intent.putExtra("BookID", bookAdapter.currentList[position].itemId)
                    requireContext().startActivity(intent)
                }
            }
        })
        binding.favRecyclerView.adapter = bookAdapter
    }
    private fun loadRecommendBooks() {
        val liveData = MutableLiveData<LoadState>()
        val resultList = ArrayList<Book>()
        searchViewModel.loadPopularBook(liveData, resultList)
        showShimmer(true)
        liveData.observe(context as MainActivity) { state ->
            if (state == LoadState.Done) {
                bookAdapter.submitList(resultList)
//                showShimmer(false)
            }

        }
    }

    //shimmer을 켜고 끄고 하는 메소드
    private fun showShimmer(bool: Boolean) {
        binding.apply {
            if (bool) SFLSearchbook.showShimmer(bool)
            llSearchbook.isVisible = !bool
            SFLSearchbook.isVisible = bool
        }
    }
}