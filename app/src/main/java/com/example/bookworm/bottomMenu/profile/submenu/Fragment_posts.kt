package com.example.bookworm.bottomMenu.profile.submenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookworm.databinding.FragmentPostsBinding

class Fragment_posts:Fragment() {
    var binding:FragmentPostsBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentPostsBinding.inflate(inflater)
        return  binding!!.root
    }
}