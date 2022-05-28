package com.example.bookworm.bottomMenu.profile.Album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookworm.databinding.FragmentAlbumNameBinding

class fragment_albumName : Fragment() {
    var binding:FragmentAlbumNameBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAlbumNameBinding.inflate(inflater)
        binding!!.btnprev.setOnClickListener({
            (context as CreateAlbumActivity).switchTab(0)
        })
        binding!!.btnnext.setOnClickListener({
            (context as CreateAlbumActivity).switchTab(1)
        })

        return binding!!.root
    }
}