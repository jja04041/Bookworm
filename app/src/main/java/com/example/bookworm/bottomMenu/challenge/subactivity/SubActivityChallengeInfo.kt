package com.example.bookworm.bottomMenu.challenge.subactivity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.challenge.ChallengeViewModel
import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.SubactivityChallengeChallengeinfoBinding

class SubActivityChallengeInfo : AppCompatActivity() {
    //뷰바인딩 정의
    val binding by lazy {
        SubactivityChallengeChallengeinfoBinding.inflate(layoutInflater)
    }

    //챌린지 뷰모델 정의
    private val challengeViewModel by lazy {
        ViewModelProvider(this, ChallengeViewModel.Factory(this))[ChallengeViewModel::class.java]
    }

    //챌린지 정보
    private val challengeInfo by lazy {
        intent.getParcelableExtra<Challenge>("challengeInfo")
    }

    //유저 뷰모델 정의
    private val userViewModel by lazy {
        ViewModelProvider(this, UserInfoViewModel.Factory(this))[UserInfoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        userViewModel.getUser(null, false)
        userViewModel.userInfoLiveData.observe(this) { mainUser ->
            setUI(mainUser)
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK) //챌린지 프레그먼트에서 새로고침을 진행하도록 함.
        finish()
        super.onBackPressed()
    }

    //UI세팅
    private fun setUI(mainUser: UserInfo) {
        binding.apply {
            //챌린지 참여를 원하는 경우
            btnChallengeJoin.setOnClickListener {
                challengeViewModel.joinChallenge(challengeInfo!!.id)
            }
            btnBack.setOnClickListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
            //프로그레스 바 설정
            progress.apply {
                progress = challengeInfo!!.currentPart.size
                max = challengeInfo!!.maxPart.toInt()
            }

        }
    }
}