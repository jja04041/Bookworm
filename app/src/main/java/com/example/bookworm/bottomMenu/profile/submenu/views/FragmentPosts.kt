package com.example.bookworm.bottomMenu.profile.submenu.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.posts.PostDisplayAdapter
import com.example.bookworm.databinding.FragmentProfileFragmentPostsBinding

class FragmentPosts(val token: String?) : Fragment() {
    var binding: FragmentProfileFragmentPostsBinding? = null
    var pv: UserInfoViewModel? = null
    lateinit var adapter: PostDisplayAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileFragmentPostsBinding.inflate(inflater)
        pv = ViewModelProvider(this, UserInfoViewModel.Factory(requireContext())).get(
            UserInfoViewModel::class.java
        )
        initRecyclerView()
        //감지 센서 부착

        pv!!.data.observe(viewLifecycleOwner, { userinfo ->
            pv!!.getFeedList(userinfo.token)
        })
        if (token != null) pv!!.getFeedList(token)
        pv!!.feedList.observe(viewLifecycleOwner, { list ->
            //받은 포스트 목록을 화면에 띄워줘야함.
            if (list.size == 0) binding!!.llalertNoPosts.visibility = View.VISIBLE
            else {
                binding!!.postRecyclerView.visibility = View.VISIBLE
                adapter.submitList(list.toList())
            }
        })

        return binding!!.root
    }

    fun initRecyclerView() {
        adapter = PostDisplayAdapter()
        binding!!.postRecyclerView.adapter = adapter
        val animator = (binding!!.postRecyclerView?.itemAnimator as SimpleItemAnimator)
        animator.supportsChangeAnimations =
            false //데이터 업데이트 시, 플리커(깜빡이는 현상) 끄기
        val gridLayoutManager = GridLayoutManager(
            context, 3, GridLayoutManager.VERTICAL, false
        )
        binding!!.postRecyclerView.setLayoutManager(gridLayoutManager);
    }

    override fun onResume() {
        super.onResume()
        if (token == null) pv!!.getUser(null, false)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}