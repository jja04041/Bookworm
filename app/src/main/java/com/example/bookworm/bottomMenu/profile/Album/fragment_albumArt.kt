package com.example.bookworm.bottomMenu.profile.Album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookworm.databinding.FragmentAlbumArtBinding

//앨범의 사진을 선택하는 화면
class fragment_albumArt :Fragment() {
    var binding:FragmentAlbumArtBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAlbumArtBinding.inflate(inflater)
        binding!!.btnnext.setOnClickListener({
            (context as CreateAlbumActivity).switchTab(2)
        })

        return binding!!.root
    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }
}