package com.example.bookworm.bottomMenu.profile.submenu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.profile.album.AlbumCreate.view.CreateAlbumActivity
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
        //initialize
        binding = FragmentAlbumsBinding.inflate(layoutInflater)
        pv = ViewModelProvider(this, UserInfoViewModel.Factory(requireContext())).get(
            UserInfoViewModel::class.java
        )


        //implements

        //감지 센서 부착
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
        return binding!!.root
    }

    override fun onResume() {
        //데이터 불러오기
        pv!!.getUser(null, false)

        super.onResume()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

}