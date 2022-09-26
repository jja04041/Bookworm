package com.example.bookworm.bottomMenu.profile.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.SubMenuPagerAdapter
import com.example.bookworm.chat.newchat.MessageActivity
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.ActivityProfileInfoBinding
import com.example.bookworm.extension.follow.view.FollowViewModelImpl
import com.example.bookworm.notification.MyFCMService
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ProfileInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileInfoBinding
    var userInfo: UserInfo? = null
    var nowUser //타인 userInfo, 현재 사용자 nowUser
            : UserInfo? = null
    lateinit var userID: String
    lateinit var fv: FollowViewModelImpl
    lateinit var userViewModel: UserInfoViewModel
    var cache: Boolean? = null
    lateinit var menuPagerAdapter: SubMenuPagerAdapter
    private var myFCMService: MyFCMService? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null

    //자신이나 타인의 프로필을 클릭했을때 나오는 화면
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fv = FollowViewModelImpl(this)
        userViewModel = ViewModelProvider(
            this,
            UserInfoViewModel.Factory(this)
        ).get(UserInfoViewModel::class.java)

        myFCMService = MyFCMService()
        mFirebaseDatabase = FirebaseDatabase.getInstance()

        //shimmer 적용을 위해 기존 뷰는 일단 안보이게, shimmer는 보이게
        binding.llResult.visibility = View.GONE
        binding.SFLoading.startShimmer()
        binding.SFLoading.visibility = View.VISIBLE

        //작성자 UserInfo (userID를 사용해 파이어베이스에서 받아옴)
        userID = intent.getStringExtra("userID")!!

        lifecycleScope.launch {
            val getSubUserjob = async { fv.getUser(userID, true) }
            val getNowUserJob = async { fv.getUser(null, true) }
            val SubUserData = getSubUserjob.await()
            nowUser = getNowUserJob.await()

            SubUserData!!.let {
                it.isFollowed = async { fv.isFollowNow(it) }.await()
                userViewModel.getBookWorm(SubUserData.token).join()
                menuPagerAdapter = SubMenuPagerAdapter(it.token, supportFragmentManager)
                Log.d("현재 읽은 도서 수 ", userViewModel.bwdata.value!!.readCount!!.toString())
                setUI(SubUserData, userViewModel.bwdata.value!!)
            }
        }

    }

    //이미 팔로잉 중
    val isFollowingTrue: Unit
        get() {
            cache = true
            binding.tvFollow.isSelected = true
            binding.tvFollow.text = "팔로잉"
            Log.d("TAG", "로그값")
        }

    //팔로잉 중이 아님
    val isFollowingFalse: Unit
        get() {
            cache = false
            binding.tvFollow.isSelected = false
            binding.tvFollow.text = "팔로우"
            Log.d("TAG", "로그값2")
        }

    //UI설정
    suspend fun setUI(user: UserInfo, bookWorm: BookWorm) {

        binding.tvNickname.text = user.username //닉네임 설정
        setMedal(user) //메달 설정
        binding.tvNickname.visibility = View.VISIBLE
        Glide.with(this).load(user.profileimg).circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.ivProfileImage) //프로필이미지 설정
        binding.ivProfileImage.visibility = View.VISIBLE

        binding.tvIntroduce.setText(user.introduce)

        // 채팅버튼
        binding.btnchatting.visibility = View.VISIBLE

        binding.btnchatting.setOnClickListener { view: View? ->
            // google id는 token길이가 매우 길기때문에 biginteger을 사용해야한다
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("opponent", (user));
            startActivity(intent)

        }


        //서브 메뉴 세팅
        binding.subMenuViewPager.adapter = menuPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.subMenuViewPager)
        binding.tabLayout.getTabAt(1)!!.text = "앨범"
        binding.tabLayout.getTabAt(0)!!.text = "포스트"
        binding.tabLayout.getTabAt(0)!!.select()
        Log.d("fragment", (binding.subMenuViewPager.adapter as SubMenuPagerAdapter).getItem(0).toString())


        if (user.isFollowed) isFollowingTrue
        else isFollowingFalse

        //내 프로필 화면이라면 팔로우 버튼 안보이게
        if (user.isMainUser) binding.tvFollow.visibility = View.GONE
        binding.tvFollowerCount.text = user.followerCounts.toString()
        binding.tvFollowingCount.text = user.followingCounts.toString()
        binding.tvReadBookCount.text = bookWorm.readCount.toString()
        binding.ivBookworm.setImageResource(
            this.resources.getIdentifier(
                "bw_${bookWorm.wormType}",
                "drawable",
                this.packageName
            )
        )


        //팔로우 버튼을 클릭했을때 버튼 모양, 상태 변경
        binding.tvFollow.setOnClickListener {
            if (binding.tvFollow.isSelected) {
                binding.tvFollow.isSelected = false
                binding.tvFollow.text = "팔로우"
                lifecycleScope.launch {
                    setFollowerCnt(
                        async { fv.follow(user, false) }
                            .await()
                            .followerCounts.toLong())
                }
            } else {
                binding.tvFollow.isSelected = true
                binding.tvFollow.text = "팔로잉"
                lifecycleScope.launch {
                    setFollowerCnt(
                        async {
                            fv.follow(user, true)
                        }.await().followerCounts.toLong()
                    )
                }
                myFCMService!!.sendPostToFCM(
                    this,
                    user!!.fCMtoken,
                    nowUser!!.username + "님이 팔로우하였습니다"
                )
            }

        }
        //뒤로가기
        binding.btnBack.setOnClickListener { view: View? ->
            if (cache != binding.tvFollow.isSelected && intent.extras!!
                    .containsKey("pos")
            ) {
                val pos = intent.getIntExtra("pos", -1)
                val intent = Intent()
                intent.putExtra("pos", pos)
                if (!cache!! && binding.tvFollow.isSelected) intent.putExtra("userInfo", user)
                setResult(100, intent)
            }
            finish()
        }
        //shimmer 적용 끝내고 shimmer는 안보이게, 기존 뷰는 보이게
        binding.llResult.visibility = View.VISIBLE
        binding.SFLoading.stopShimmer()
        binding.SFLoading.visibility = View.GONE
    }

    fun setFollowerCnt(count: Long) {
        binding.tvFollowerCount.text = count.toString()
    }

    //메달 표시 유무에 따른 세팅
    private fun setMedal(userInfo: UserInfo) {
        if (userInfo.medalAppear!!) { //메달을 표시한다면
            binding!!.ivMedal.setVisibility(View.VISIBLE)
            when (userInfo.tier.toString().toInt()) {
                1 -> binding.ivMedal.setImageResource(R.drawable.medal_bronze)
                2 -> binding.ivMedal.setImageResource(R.drawable.medal_silver)
                3 -> binding.ivMedal.setImageResource(R.drawable.medal_gold)
                4 -> {}
                5 -> {}
                else -> binding.ivMedal.setImageResource(0)
            }
        } else { //메달을 표시하지 않을거라면
            binding!!.ivMedal.setVisibility(View.GONE)
            binding!!.ivMedal.setImageResource(0)
        }
    }
}