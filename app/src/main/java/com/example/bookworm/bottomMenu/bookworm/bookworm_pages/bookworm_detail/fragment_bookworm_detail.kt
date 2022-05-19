package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.achievement.activity_achievement
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.FragmentBwLayoutBinding
import com.example.bookworm.notification.MyFCMService
import com.google.firebase.database.FirebaseDatabase

class fragment_bookworm_detail : Fragment() {
    var binding: FragmentBwLayoutBinding? =null
    lateinit var uv:UserInfoViewModel
    lateinit private var myFCMService: MyFCMService
    lateinit private var  mFirebaseDatabase: FirebaseDatabase



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentBwLayoutBinding.inflate(layoutInflater)
        uv = ViewModelProvider(
            this, UserInfoViewModel.Factory(requireContext())
        ).get(
            UserInfoViewModel::class.java
        )
        myFCMService = MyFCMService()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        binding!!.btnAchievementBg.setOnClickListener({
            val intent = Intent(requireContext(), activity_achievement::class.java)
            // 1이면 activity achievement에서  bookworm 보여주게
            intent.putExtra("type", 1)
            startActivity(intent)
        })
        binding!!.btnAchievement.setOnClickListener({
            val intent = Intent(requireContext(), activity_achievement::class.java)
            // 1이면 activity achievement에서  bg 보여주게
            intent.putExtra("type", 0)
            startActivity(intent)
        })
        return binding!!.root
    }
    override fun onResume() {
        super.onResume()
        uv.getUser(null, false)
        //사용자 데이터를 추적
        uv.data.observe(this) { userInfo: UserInfo ->
            uv.getBookWorm(userInfo.token) //책볼레 데이터를 불러오도록 한다.
        }
        //책벌레 데이터의 도착 여부 추적
        uv.bwdata.observe(this) { bw: BookWorm ->
            //데이터가 도착한 경우
            binding!!.ivBookworm.setImageResource(bw.wormtype)
            binding!!.ivBg.setImageResource(bw.bgtype)
        }
        binding!!.ivBg.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
        binding!!.ivBg.setAdjustViewBounds(true)
    }

        override fun onDestroy() {
        binding=null
        super.onDestroy()
    }
}