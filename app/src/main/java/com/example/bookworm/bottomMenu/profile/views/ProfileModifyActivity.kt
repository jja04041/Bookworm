package com.example.bookworm.bottomMenu.profile.views

import android.app.AlertDialog
import android.content.Context

import androidx.appcompat.app.AppCompatActivity
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.challenge.ChallengeViewModel
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.content.Intent

import android.graphics.Bitmap
import android.widget.Toast
import android.text.TextWatcher
import android.text.Editable
import android.content.DialogInterface
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.example.bookworm.core.login.LoginActivity

import com.example.bookworm.R

import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.ActivityProfileModifyBinding
import com.kakao.sdk.user.UserApiClient

class ProfileModifyActivity : AppCompatActivity() {
    private lateinit var imageProcess: ImageProcessing

    companion object {
        const val MODIFY_OK = 2003
    }

    val binding by lazy {
        ActivityProfileModifyBinding.inflate(layoutInflater)
    }
    private val userInfoViewModel by lazy {
        ViewModelProvider(this, UserInfoViewModel.Factory(this))[UserInfoViewModel::class.java]
    }
    private val challengeViewmodel by lazy {
        ViewModelProvider(this, ChallengeViewModel.Factory(this))[ChallengeViewModel::class.java]
    }
    private var isModified = false
    private var uploadCheck = false
    lateinit var imm: InputMethodManager
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val rect = Rect()
            currentFocus!!.getGlobalVisibleRect(rect)
            if (!rect.contains(ev!!.x.toInt(), ev.y.toInt())) {
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                currentFocus!!.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun closeLogic() {
        if (isModified) setResult(MODIFY_OK)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val isDuplicated: LiveData<Boolean> = userInfoViewModel.isDuplicated
        imageProcess = ImageProcessing(this)

        binding.apply {
            userInfoViewModel.apply {
                getUser(null, false)
                userInfoLiveData.observe(this@ProfileModifyActivity)
                { nowUser: UserInfo ->
                    binding.tvNickname.text = nowUser!!.username
                    setMedal(nowUser)
                    Glide.with(this@ProfileModifyActivity)
                        .load(nowUser.profileimg)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(ivProfileImage)
                    edtIntroduce.setText(nowUser!!.introduce) //자기소개 세팅
                    //회원탈퇴 버튼
                    btnSignout.setOnClickListener { v: View? ->
                        AlertDialog.Builder(this@ProfileModifyActivity)
                            .setMessage("탈퇴하시겠습니까?")
                            .setPositiveButton("네") { dialog: DialogInterface, which: Int ->
                                signOut(nowUser.platform!!)
                                dialog.dismiss()
                            }
                            .setNegativeButton("아니요") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                            .show()
                    }
                    imageProcess!!.bitmapUri.observe(this@ProfileModifyActivity) { observer: Uri? ->
                        Glide.with(root).load(observer).circleCrop()
                            .into(ivProfileImage)
                    }
                    //프로필이미지를 업로드 하는 경우
                    imageProcess!!.bitmap.observe(this@ProfileModifyActivity) { bitmap: Bitmap? ->
                        //완료버튼을 누르면 이미지 업데이트
                        btnFinish.setOnClickListener {
                            val imgName = "profile_" + nowUser!!.token + ".jpg"
                            imageProcess.uploadImage((bitmap)!!, imgName) // 이미지 업로드
                        }
                        imageProcess.imgData.observe(this@ProfileModifyActivity) { imgurl: String? ->
                            if (checkIdChanged()) {
                                nowUser.profileimg = (imgurl)!!
                                updateUser(nowUser)
                                isModified = true
                                closeLogic()
                            } else {
                                Toast.makeText(
                                    this@ProfileModifyActivity,
                                    "아이디 중복 체크 후 진행해 주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    btnFinish.setOnClickListener {
                        if (uploadCheck) {
                            userInfoViewModel.updateUser(nowUser)
                            isModified = true
                            closeLogic()
                        } else if (nowUser.username == tvNickname.text.toString()) closeLogic()
                        else
                            Toast.makeText(
                                this@ProfileModifyActivity,
                                "아이디 중복 체크 후 진행해 주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                    btnFavGenre.setOnClickListener {
                        val intent =
                            Intent(this@ProfileModifyActivity, PreferGenreActivity::class.java)
                        startActivity(intent)
                    }
                    edtNewNickname.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            charSequence: CharSequence,
                            i: Int,
                            i1: Int,
                            i2: Int
                        ) {
                        }

                        override fun onTextChanged(
                            charSequence: CharSequence,
                            i: Int,
                            i1: Int,
                            i2: Int
                        ) {
                            uploadCheck = false
                        }

                        override fun afterTextChanged(editable: Editable) {
                            uploadCheck = checkIdChanged()
                        }
                    })
                    isDuplicated.observe(this@ProfileModifyActivity) { value: Boolean? ->
                        if (!value!!) {
                            nowUser.username = edtNewNickname.text.toString()
                            uploadCheck = true
                            Toast.makeText(
                                this@ProfileModifyActivity,
                                "사용 가능한 닉네임입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else Toast.makeText(
                            this@ProfileModifyActivity,
                            "사용할 수 없는 닉네임입니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    //자기소개 수정
                    btnIntroModify.apply {
                        setOnClickListener {
                            if (text.toString() == "수정") { //수정하기 버튼을 눌렀을 때
                                text = "완료"
                                edtIntroduce.apply {
                                    val imm = this@ProfileModifyActivity.getSystemService(
                                        INPUT_METHOD_SERVICE
                                    ) as InputMethodManager
                                    imm.toggleSoftInput(
                                        InputMethodManager.SHOW_FORCED,
                                        InputMethodManager.HIDE_IMPLICIT_ONLY
                                    )
                                    setBackgroundColor(Color.parseColor("#2200ff00")) //배경색 변경
                                    isEnabled = true//EditText를 수정 가능한 상태로 만듦
                                    requestFocus()//초점 이동
                                    setSelection(length()) //가장 마지막으로 커서 이동
                                }
                                btnModifyCancle.isVisible = true //수정 취소 버튼 노출
                            } else { //완료 버튼을 눌렀을 때
                                AlertDialog.Builder(this@ProfileModifyActivity) //변경하
                                    .setMessage("변경하시겠습니까?")
                                    .setPositiveButton("네") { dialog: DialogInterface, which: Int ->
                                        nowUser!!.introduce =
                                            edtIntroduce.text.toString() //userinfo의 자기소개를 변경
                                        userInfoViewModel.updateUser(nowUser) // 사용자 정보 업데이트
                                        isModified = true
                                        text = "수정" //완료 버튼에서 다시 수정 버튼으로 변경
                                        edtIntroduce.setBackgroundColor(Color.WHITE) //배경색 변경
                                        edtIntroduce.isEnabled = false //수정 불가능하게 변경
                                        btnModifyCancle.isVisible = false //수정 취소 버튼 숨김

                                        //키보드 내리기
                                        val imm = this@ProfileModifyActivity.getSystemService(
                                            INPUT_METHOD_SERVICE
                                        ) as InputMethodManager
                                        imm.hideSoftInputFromWindow(
                                            binding!!.edtIntroduce.windowToken,
                                            0
                                        )
                                        edtIntroduce.clearFocus() //초점 제거
                                        dialog.dismiss()
                                    }
                                    .setNegativeButton("아니요") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                                    .show()
                            }
                        }
                    }

                    btnModifyCancle.apply {
                        setOnClickListener {
                            isVisible = false//수정 취소 버튼 숨김
                            btnIntroModify.text = "수정" //완료 버튼에서 다시 수정 버튼으로 변경
                            edtIntroduce.setBackgroundColor(Color.WHITE) //배경색 변경
                            edtIntroduce.isEnabled = false //수정 불가능하게 변경
                            edtIntroduce.setText(nowUser!!.introduce) //기존의 자기소개로 변경
                        }
                        //키보드 내리기
                        val imm =
                            this@ProfileModifyActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding!!.edtIntroduce.windowToken, 0)
                        edtIntroduce.clearFocus() //초점 제거
                    }
                    //메달 관련

                    if (nowUser!!.medalAppear!!) { //메달 표시가 true일때
                        binding!!.setMedalInNickname.isSelected = true
                        binding!!.setMedalInNickname.text = "표시"
                    } else { //메달 표시가 false일때
                        binding!!.setMedalInNickname.isSelected = false
                        binding!!.setMedalInNickname.text = "숨김"
                    }
                    binding!!.setMedalInNickname.setOnClickListener {
                        if (binding!!.setMedalInNickname.isSelected) {
                            nowUser!!.medalAppear = false
                            binding!!.setMedalInNickname.isSelected = false
                            binding!!.setMedalInNickname.text = "숨김"
                            userInfoViewModel!!.updateUser(nowUser!!)
                            setMedal(nowUser)
                        } else {
                            nowUser!!.medalAppear = true
                            binding!!.setMedalInNickname.isSelected = true
                            binding!!.setMedalInNickname.text = "표시"
                            userInfoViewModel!!.updateUser(nowUser!!)
                            setMedal(nowUser)
                        }
                    }

                    //로그아웃 버튼
                    binding.btnLogout.setOnClickListener { v: View? ->
                        AlertDialog.Builder(this@ProfileModifyActivity)
                            .setMessage("로그아웃하시겠습니까?")
                            .setPositiveButton("네") { dialog, which ->
                                Toast.makeText(
                                    this@ProfileModifyActivity,
                                    "정상적으로 로그아웃되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (GoogleSignIn.getLastSignedInAccount(this@ProfileModifyActivity) != null) {
                                    LoginActivity.gsi!!.signOut()
                                    userInfoViewModel.logOut()
                                    val intent = Intent(
                                        this@ProfileModifyActivity,
                                        LoginActivity::class.java
                                    )
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    startActivity(intent)
                                    finish()
                                    dialog.dismiss()
                                } else {
                                    //카카오 로그아웃 구현
                                    UserApiClient.instance.logout { error ->
                                        if (error != null) {
                                            Toast.makeText(
                                                this@ProfileModifyActivity,
                                                "로그아웃 실패 $error",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@ProfileModifyActivity,
                                                "로그아웃 성공",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            userInfoViewModel.logOut()
                                            val intent = Intent(
                                                this@ProfileModifyActivity,
                                                LoginActivity::class.java
                                            )
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)
                                            finish()
                                            dialog.dismiss()
                                        }
                                    }
                                }

                            }
                            .setNegativeButton("아니요") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                            .show()
                    }

                }


            }













            challengeViewmodel.challengeList.observe(this@ProfileModifyActivity)
            {
                for (i in it) {
                    Log.d("챌린지 데이터: ", i.masterToken)
                }
            }
            tvProfileImageModify.setOnClickListener { imageProcess!!.initProcess() }
            binding!!.checkDuplicate.setOnClickListener {
                val name = binding!!.edtNewNickname.text.toString()
                if (!name.contains(" ") && name != "") userInfoViewModel.checkDuplicate(name)
                else Toast.makeText(
                    this@ProfileModifyActivity,
                    "닉네임에는 공백을 넣을 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //뒤로가기 버튼
            binding!!.btnBack.setOnClickListener { finish() }
        }
    }


    private fun checkIdChanged(): Boolean {
        val name = binding!!.edtNewNickname.text.toString()
        return if (name == "") true else !(name != "" && !uploadCheck)
    }

    //메달 표시 유무에 따른 세팅
    private fun setMedal(userInfo: UserInfo?) {
        if (userInfo!!.medalAppear!!) { //메달을 표시한다면
            binding!!.ivMedal.visibility = View.VISIBLE
            when (userInfo.tier.toString().toInt()) {
                1 -> binding!!.ivMedal.setImageResource(R.drawable.medal_bronze)
                2 -> binding!!.ivMedal.setImageResource(R.drawable.medal_silver)
                3 -> binding!!.ivMedal.setImageResource(R.drawable.medal_gold)
                4 -> {}
                5 -> {}
                else -> binding!!.ivMedal.setImageResource(0)
            }
        } else { //메달을 표시하지 않을거라면
            binding!!.ivMedal.visibility = View.GONE
            binding!!.ivMedal.setImageResource(0)
        }
    }

    //로그인 액티비티로 이동
    fun moveToLogin() {
        val intent = Intent(this@ProfileModifyActivity, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }


    //회원탈퇴
    private fun signOut(platFormName: String) {
        when (platFormName) {
            "Kakao" -> {
                UserApiClient.instance.unlink { error ->
                    if (error != null) {
                        //연결 해제 실패(회원 탈퇴 실패)
                        Toast.makeText(
                            this@ProfileModifyActivity,
                            "회원탈퇴에 실패했습니다. 다시 시도해 주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //성공
                        signOutFB() //파이어 스토어에서도 계정 삭제
                    }

                }
            }
            else -> {//구글 회원탈퇴 메소드
                LoginActivity.gsi!!.revokeAccess()
                signOutFB()
            }
        }


    }


    private fun signOutFB() {
        val liveData = MutableLiveData<UserInfoViewModel.State>()
        userInfoViewModel.deleteUser(liveData)
        liveData.observe(this@ProfileModifyActivity) { state ->
            when (state) {
                //성공
                UserInfoViewModel.State.Done -> {
                    Toast.makeText(this@ProfileModifyActivity, "회원탈퇴에 성공했습니다.", Toast.LENGTH_SHORT)
                        .show()
                    moveToLogin()
                }
                //에러날 시에
                else -> {
                    Log.e("회원탈퇴 오류", "파이어스토어에서 계정 삭제 불가")
                }
            }
        }
    }

    override fun onBackPressed() {
        closeLogic()
        super.onBackPressed()
    }
}