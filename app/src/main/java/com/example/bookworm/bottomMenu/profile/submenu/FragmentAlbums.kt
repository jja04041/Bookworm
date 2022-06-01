package com.example.bookworm.bottomMenu.profile.submenu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.profile.Album.view.CreateAlbumActivity
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.databinding.FragmentAlbumsBinding


class FragmentAlbums : Fragment() {
    var binding: FragmentAlbumsBinding? = null
    var pv: UserInfoViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumsBinding.inflate(layoutInflater)
        pv = ViewModelProvider(this, UserInfoViewModel.Factory(requireContext())).get(
            UserInfoViewModel::class.java
        )
        pv!!.data.observe(viewLifecycleOwner, { userinfo ->
            pv!!.getFeedList(userinfo.token)
        })
        pv!!.feedList.observe(viewLifecycleOwner,{ list->
            binding!!.btnAddAlbum.setOnClickListener({
                var intent = Intent(context,CreateAlbumActivity::class.java)
                intent.putExtra("list",list)
                startActivity(intent)
            })
        })
        //앨범 추가 버튼 클릭시
//        pv.getFeedList().observe(getViewLifecycleOwner(), list -> {
//            binding.btnAddAlbum.setOnClickListener(it -> {
//                Intent intent = new Intent(current_context, CreateAlbumActivity.class);
//                intent.putExtra("list", list);
//                startActivity(intent);
//            });
//        });
        return binding!!.root
    }

    override fun onResume() {
        pv!!.getUser(null, false)
        super.onResume()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

}