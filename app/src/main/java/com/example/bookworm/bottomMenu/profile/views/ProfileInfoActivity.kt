package com.example.bookworm.bottomMenu.profile.views

//import com.example.bookworm.Extension.Follow.Modules.followCounter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.ActivityProfileInfoBinding
import com.example.bookworm.extension.follow.view.FollowViewModel
import com.example.bookworm.notification.MyFirebaseMessagingService
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProfileInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileInfoBinding
    var userInfo: UserInfo? = null
    var nowUser //타인 userInfo, 현재 사용자 nowUser
            : UserInfo? = null
    lateinit var userID: String
    lateinit var fv: FollowViewModel
    var cache: Boolean? = null

    private var myFirebaseMessagingService: MyFirebaseMessagingService? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null

    //자신이나 타인의 프로필을 클릭했을때 나오는 화면
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fv = FollowViewModel(this)

        myFirebaseMessagingService = MyFirebaseMessagingService()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        //일단 안보였다가 파이어베이스에서 값을 모두 받아오면 보여주는게 UX면에서 좋을거같음
//        binding.tvNickname.setVisibility(View.INVISIBLE);
//        binding.ivProfileImage.setVisibility(View.INVISIBLE);
//        binding.tvFollow.setVisibility(View.INVISIBLE);

        //shimmer 적용을 위해 기존 뷰는 일단 안보이게, shimmer는 보이게
        binding.llResult.visibility = View.GONE
        binding.SFLoading.startShimmer()
        binding.SFLoading.visibility = View.VISIBLE

        //작성자 UserInfo (userID를 사용해 파이어베이스에서 받아옴)
        userID = intent.getStringExtra("userID")!!


        lifecycleScope.launch {
            val getSubUserjob = async { fv.getUser(userID) }
            val data = getSubUserjob.await()
            data!!.let {
                data.isFollowed = async { fv.isFollowNow(it) }.await()
                setUI(data)
            }
        }


        //        binding.btnFollower.setOnClickListener((view)-> {
//            Intent intent=new Intent(context, FollowerActivity.class);
//            intent.putExtra("token",userInfo.getToken());
//            intent.putExtra("page",0);
//            context.startActivity(intent);
//        });
//        binding.btnFollowing.setOnClickListener((view)-> {
//            Intent intent=new Intent(context, FollowerActivity.class);
//            intent.putExtra("token",userInfo.getToken());
//            intent.putExtra("page",1);
//            context.startActivity(intent);
//        });
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
    suspend fun setUI(user: UserInfo) {

        binding.tvNickname.text = user.username //닉네임 설정
        binding.tvNickname.visibility = View.VISIBLE
        Glide.with(this).load(user.profileimg).circleCrop()
            .into(binding.ivProfileImage) //프로필이미지 설정
        binding.ivProfileImage.visibility = View.VISIBLE

        if (user.isFollowed) isFollowingTrue
        else isFollowingFalse

        //내 프로필 화면이라면 팔로우 버튼 안보이게
        if (user.isMainUser) binding.tvFollow.visibility = View.GONE
        binding.tvFollowCount.text = user.followerCounts.toString()


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
                        async { fv.follow(user, true) }
                            .await()
                            .followerCounts.toLong()
                    )
                }
                myFirebaseMessagingService!!.sendPostToFCM(user!!.fcMtoken,  "님이 팔로우하였습니다")
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
        binding.tvFollowCount.text = count.toString()
    }
}