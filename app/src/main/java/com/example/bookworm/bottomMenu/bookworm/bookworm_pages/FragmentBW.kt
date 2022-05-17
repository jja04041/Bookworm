package com.example.bookworm.bottomMenu.bookworm.bookworm_pages

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
import com.example.bookworm.notification.MyFirebaseMessagingService
import com.google.firebase.database.FirebaseDatabase

class FragmentBW : Fragment() {
    var binding: FragmentBwLayoutBinding? =null
    lateinit var uv:UserInfoViewModel
    lateinit private var myFirebaseMessagingService: MyFirebaseMessagingService
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
        myFirebaseMessagingService = MyFirebaseMessagingService()
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
        uv.data.observe(this) { userInfo: UserInfo ->
            val genre = arrayOf(
                "자기계발",
                "소설",
                "육아",
                "어린이",
                "청소년",
                "사회",
                "과학",
                "인문",
                "생활",
                "공부",
                "만화"
            )
            val bookworm = arrayOf(
                binding!!.tvBookworm1,
                binding!!.tvBookworm2,
                binding!!.tvBookworm3,
                binding!!.tvBookworm4,
                binding!!.tvBookworm5,
                binding!!.tvBookworm6,
                binding!!.tvBookworm7,
                binding!!.tvBookworm8,
                binding!!.tvBookworm9,
                binding!!.tvBookworm10,
                binding!!.tvBookworm11
            )
            for (i in genre.indices) {
                if (userInfo.genre[genre[i]] != null) bookworm[i]
                    .append(genre[i] + " : " + userInfo.genre[genre[i]])
            }
            uv.getBookWorm(userInfo.token)
        }
        uv.bwdata.observe(this) { bw: BookWorm ->
            binding!!.ivBookworm.setImageResource(bw.wormtype)
            binding!!.ivBg.setImageResource(bw.bgtype)
            binding!!.tvBookwormBookcount.setText("읽은 권 수 : " + bw.readcount)
        }
        binding!!.ivBg.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
        binding!!.ivBg.setAdjustViewBounds(true)
    }

        override fun onDestroy() {
        binding=null
        super.onDestroy()
    }
}