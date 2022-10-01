package com.example.bookworm.bottomMenu.challenge.subactivity

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bookworm.LoadState
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
        val liveData = MutableLiveData<UserInfo>()
        userViewModel.getUser(null, liveData = liveData, false)
        liveData.observe(this) { userInfo ->
            setUI(userInfo)
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
                val liveData = MutableLiveData<LoadState>()
                challengeViewModel.joinChallenge(challengeInfo!!.id, mainUser.token, liveData)
                liveData.observe(this@SubActivityChallengeInfo) { state ->
                    //로딩이 종료되면 실행
                    if (state != LoadState.Loading) when (state) {
                        //정상적으로 참여 완료
                        LoadState.Done -> {
                            btnChallengeJoin.apply {
                                isEnabled = false
                                text = "참여중인 챌린지입니다."
                            }
                        }
                        //참여 제한
                        LoadState.OverError -> {
                            Toast.makeText(this@SubActivityChallengeInfo,
                                    "정원이 초과되었습니다. 아쉽지만 다른 챌린지를 시도해 주세요.",
                                    Toast.LENGTH_SHORT).show()
                            binding.btnChallengeJoin.apply {
                                isEnabled = false
                                text = "정원이 초과된 챌린지입니다."
                            }
                        }
                        //파베 오류
                        else -> {
                            Toast.makeText(this@SubActivityChallengeInfo,
                                    "참여도중 에러가 발생하였습니다. 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            //뒤로 가려는 경우
            btnBack.setOnClickListener {
                setResult(Activity.RESULT_OK)
                finish()
            }

            //챌린지 데이터 세팅
            challengeInfo!!.apply {
                book.apply {
                    //책 썸네일 세팅
                    Glide.with(this@SubActivityChallengeInfo)
                            .load(this.imgUrl).into(ivThumbnail)
                    //책제목 세팅
                    tvChallengeinfoBookname.text = title
                    tvChallengeinfoBookname.isSingleLine = true
                    tvChallengeinfoBookname.ellipsize = TextUtils.TruncateAt.MARQUEE //흐르게 만들기
                    tvChallengeinfoBookname.isSelected = true //선택하기
                }
                //프로그레스 바 설정
                progress.apply {
                    progress = currentPart.size
                    max = maxPart.toInt()
                }
                //챌린지 정보 설정
                tvChallengeTitle.text = title //챌린지 제목
                tvChallengeDescription.text = description //챌린지 설명
                tvChallengeinfoEnd.text = endDate //종료 일자
                tvCurrentParticipants.text = currentPart.size.toString() //현재 참여자 수


            }

        }
    }
}