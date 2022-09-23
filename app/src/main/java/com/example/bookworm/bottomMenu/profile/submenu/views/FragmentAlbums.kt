package com.example.bookworm.bottomMenu.profile.submenu.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view.CreateAlbumContentActivity
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.item.AlbumDisplayAdapter
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.view.ShowAlbumContentActivity
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.FragmentProfileFragmentAlbumsBinding

//본인 앨범을 보는 경우, null이 전달됨 .
class FragmentAlbums(val token: String?) : Fragment() {
    var binding: FragmentProfileFragmentAlbumsBinding? = null
    var pv: UserInfoViewModel? = null
    lateinit var adapter: AlbumDisplayAdapter
    var NowUser: UserInfo? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //initialize
        binding = FragmentProfileFragmentAlbumsBinding.inflate(layoutInflater)
        pv = ViewModelProvider(this, UserInfoViewModel.Factory(requireContext())).get(
            UserInfoViewModel::class.java
        )
        initRecyclerView()

        //implements

        //감지 센서 부착
        pv!!.userInfoLiveData.observe(viewLifecycleOwner) { userinfo ->
            if (NowUser == null) NowUser = userinfo
            if (token != null && userinfo.token != token) {
                pv!!.getFeedList(token!!)
                pv!!.getalbums(token!!)
            } else {
                pv!!.getFeedList(userinfo.token)
                pv!!.getalbums(userinfo.token)
            }
        }

        pv!!.feedList.observe(viewLifecycleOwner, { list ->
            if (token == NowUser!!.token || token == null) binding!!.btnAddAlbum.visibility =
                View.VISIBLE
            binding!!.btnAddAlbum.setOnClickListener({
//                var intent = Intent(context, CreateAlbumActivity::class.java)
                var intent = Intent(context, CreateAlbumContentActivity::class.java)
                intent.putExtra("list", list) //사용자가 작성한 포스트의 목록
                startActivity(intent)
            })
        })

        pv!!.albumdata.observe(viewLifecycleOwner, { albumlist ->
            adapter.submitList(albumlist.toList()) {
                //데이터가 있는 경우에만 리사이클러뷰를 화면에 표시한다.
                if (adapter.currentList.size == 0) {
                    binding!!.llalertNoAlbums.visibility = View.VISIBLE
                    binding!!.albumRecyclerView.visibility = View.INVISIBLE;
                } else {
                    binding!!.albumRecyclerView.visibility = View.VISIBLE
                    binding!!.llalertNoAlbums.visibility = View.INVISIBLE
                }
            }
        })

        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        //사용자 데이터 불러오기
        pv!!.getUser(null, false)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    fun initRecyclerView() {
        adapter = AlbumDisplayAdapter(requireContext())
        adapter.setOnItemClickListener(object : AlbumDisplayAdapter.OnViewHolderItemClickListener {
            override fun onViewHolderItemClick(view: View, position: Int) {
                val albumItem = adapter.currentList[position]
                var intent = Intent(context, ShowAlbumContentActivity::class.java)
                intent.putExtra("albumData",albumItem)
                startActivity(intent)
            }

        })
        binding!!.albumRecyclerView.adapter = adapter

        (binding!!.albumRecyclerView?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false //데이터 업데이트 시, 플리커(깜빡이는 현상) 끄기
        val gridLayoutManager = GridLayoutManager(
            context, 2, GridLayoutManager.VERTICAL, false
        )
        binding!!.albumRecyclerView.setLayoutManager(gridLayoutManager);
    }
}