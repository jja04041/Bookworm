package com.example.bookworm.core.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.R
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.views.PreferGenreActivity
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient


class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private var mAuth: FirebaseAuth? = null
    private var gsa: GoogleSignInAccount? = null
    var userInfo: UserInfo? = null
    var isLogined: Boolean = java.lang.Boolean.FALSE
    private val userViewModel by lazy {
        ViewModelProvider(
                this, UserInfoViewModel.Factory(this)
        )[UserInfoViewModel::class.java]
    }
    var startActivityResult = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            task.addOnCompleteListener { task1: Task<GoogleSignInAccount> ->
                //회원의 정보를 가져옴
                val account = task1.result
                gsa = task1.result
                Log.d("구글 로그인 데이터", task1.result.idToken!!)
                //회원 정보 객체를 생성
                userInfo = UserInfo(
                        username = account.displayName!!,
                        token = account.id!!,
                        profileimg = try {
                            account.photoUrl.toString()!!
                        } catch (e: NullPointerException) {
                            ""
                        },
                        platform = "Google"
                )
                //회원 가입 함수로 데이터를 전달
                signUp(userInfo!!)
            }
        }
    }

    //카카오톡으로 로그인 할 수 없어 카카오 계정으로 로그인 할 경우 이용
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            when (error.toString()) {
                AuthErrorCause.AccessDenied.toString() -> {
                    //접근이 거부됨 (동의 취소)
                }
                else -> {
                    //기타 에러
                }
            }


            //카카오계정으로 로그인 실패
        } else if (token != null) {
            //카카오계정으로 로그인 성공
            createUserByKakao()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        isLogined = java.lang.Boolean.FALSE //카카오 이중로그인 방지
        mAuth = FirebaseAuth.getInstance()
        startLogin()

    }

    private fun startLogin() {

        // 구글

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build()
        gsi = GoogleSignIn.getClient(this, gso)
        gsa = GoogleSignIn.getLastSignedInAccount(this)
        // 구글 자동 로그인
        if (gsa != null) {
            signInGoogle()
            return
        }
        binding.apply {
            btnLoginGoogle.setOnClickListener {
                signInGoogle()
            }
            btnLoginKakao.setOnClickListener {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
                    UserApiClient.instance.loginWithKakaoAccount(this@LoginActivity) { token, error ->
                        if (error != null) {

                            //사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                            // 의도적인 로그인 취소로 보고 카카오 계저응로 로그인 시도 없이 로그인 취소로 처리
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoAccount
                            }

                            //카카오톡에 연결된 카카오 계정이 없는 경우, 카카오 계정으로 로그인 시도
                            UserApiClient.instance.loginWithKakaoAccount(context = this@LoginActivity, callback = callback)
                        } else if (token != null) {
                            //카카오 톡으로 로그인 성공
                            createUserByKakao()
                        }
                    }
                } else UserApiClient.instance.loginWithKakaoAccount(context = this@LoginActivity, callback = callback)
            }

        }
    }


    private fun createUserByKakao() {
        UserApiClient.instance.me { user, error ->
            signUp(UserInfo(
                    username = user!!.kakaoAccount!!.profile!!.nickname!!,
                    token = user.id.toString(),
                    profileimg = user.kakaoAccount!!.profile!!.profileImageUrl!!,
                    platform = "Kakao"
            ))
        }
    }


    //메인액티비티로 이동
    private fun StartApplication(_iFlag: Int) {

        /** flag 0 == 가입, 1 == login */
        if(_iFlag == 0)
        {
            val intent = Intent(this, PreferGenreActivity::class.java)
            intent.putExtra("Login", 1)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
        if(_iFlag == 1)
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }

    //회원가입 함수
    fun signUp(userInfo: UserInfo) {
        if (gsa != null) {
            val accessToken = gsa!!.idToken
            val credential = GoogleAuthProvider.getCredential(accessToken, null)
            mAuth!!.signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult> -> Log.d("로그인 완료", task.result.toString()) }
        }
        userViewModel.getUser(userInfo.token, true) //회원 여부 확인을 위한 회원정보 조회
        CheckFcm(userInfo)
        userViewModel.userInfoLiveData.observe(this) { userinfo: UserInfo? ->
            //회원인 경우
            if (userinfo!!.platform != null) StartApplication(1)
            else {
                val userInfoLiveData = MutableLiveData<Boolean>()
                //사용자 생성
                userViewModel.createUser(userInfo, userInfoLiveData)
                //액티비티 이동
                userInfoLiveData.observe(this) { et: Boolean -> if (et) StartApplication(0) }
            }
        }
    }

    private fun CheckFcm (userInfo: UserInfo) {
        userViewModel.getUser(userInfo!!.token, true)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                if(userInfo!!.fCMtoken != task.result || userInfo!!.fCMtoken == null)
                    userInfo!!.fCMtoken = task.result
            }
        }
    }

    // 구글 로그인 메소드
    private fun signInGoogle() {
        val signInIntent = gsi!!.signInIntent
        startActivityResult.launch(signInIntent)
    }

    companion object {

        @JvmField
        var gsi: GoogleSignInClient? = null
    }
}