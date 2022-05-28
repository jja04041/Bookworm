package com.example.bookworm.bottomMenu.profile.Album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookworm.databinding.FragmentSelectFeedBinding

//앨범에 담을 피드를 선택하는 화면
class fragment_selectFeed :Fragment() {
    var binding:FragmentSelectFeedBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectFeedBinding.inflate(inflater)
        binding!!.btnprev.setOnClickListener({
            (context as CreateAlbumActivity).switchTab(1)
        })
        binding!!.btnDone.setOnClickListener({
            (context as CreateAlbumActivity).finish()
        })

        return binding!!.root
    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }
}