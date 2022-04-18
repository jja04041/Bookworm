package com.example.bookworm.Follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.Feed.Comments.DiffUtilCallback
import com.example.bookworm.User.UserInfo
import com.example.bookworm.databinding.FragmentProfileFollowerBinding
import com.example.bookworm.modules.personalD.PersonalD

class fragment_profile_following(val token: String) : Fragment() {
    var frame: FollowerFrame? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        frame = FollowerFrame(token, context, layoutInflater, false)
        return frame!!.getView()
    }

    override fun onDestroy() {
        super.onDestroy()
        frame!!.setBindingNull()
    }
}