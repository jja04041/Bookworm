package com.example.bookworm.core.userdata

import android.content.Context
import android.util.Log
import com.example.bookworm.bottomMenu.profile.Album.item.AlbumData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.Exclude
import com.kakao.usermgmt.response.model.UserAccount
import java.io.Serializable
import java.util.*

class UserInfo : Serializable {
    var profileimg: String? = null // 회원가입시 프로필사진
    var username = "(알 수 없음)" // 회원가입시 닉네임
    private var email: String? = null // 로그인한 이메일
    var platform: String? = null //플랫폼 확인
    var introduce = "안녕하세요~"

    @get:Exclude
    var isMainUser = false //메인 유저인지 확인하는 변수

    @get:Exclude
    var isFollowed = false //팔로우 여부 확인 하는 변수

    private val albumData: ArrayList<AlbumData> //앨범리스트
    var fCMtoken: String? = null
    lateinit var token: String
    var likedPost: ArrayList<String>?
    var followerCounts = 0
    var followingCounts = 0
    var genre: HashMap<String, Int?>? = null


    init {
        genre = HashMap()
        likedPost = ArrayList()
        albumData = ArrayList()
    }


    //구글 계정 데이터
    fun add(account: GoogleSignInAccount) {
        try {
            Log.d("profile", account.photoUrl.toString())
            profileimg = account.photoUrl.toString()
        } catch (e: NullPointerException) {
            Log.d("profile", "Null")
        }
        username = account.displayName!!
        email = account.email
        platform = "Google"
    }

    //카카오 계정 데이터
    fun add(kakaoAccount: UserAccount) {
        val profile = kakaoAccount.profile
        profileimg = profile.profileImageUrl
        username = profile.nickname
        email = kakaoAccount.email
        platform = "Kakao"
    }

    //파이어베이스에서 값을 가져옴
    fun add(document: Map<*, *>) {
        username = document["username"] as String
        email = document["email"] as String?
        profileimg = document["profileimg"] as String?
        token = document["token"] as String
        platform = document["platform"] as String?
        followerCounts = document["followerCounts"].toString().toInt()
        followingCounts = document["followingCounts"].toString().toInt()
        genre = HashMap(document["genre"] as HashMap<String, Int?>?)
        if (document["likedPost"] as ArrayList<String?>? != null) likedPost =
            document["likedPost"] as ArrayList<String>? else likedPost = ArrayList()
        fCMtoken = document["fcmtoken"] as String
        if (document["introduce"] as String? != null)
            introduce = (document["introduce"] as String?)!!

    }

    //장르 설정
    fun setGenre(categoryname: String?, context: Context?) {
        val tokenizer = StringTokenizer(categoryname, ">")
        tokenizer.nextToken() // 두번째 분류를 원하기 때문에 맨 앞 분류 꺼냄
        var category = tokenizer.nextToken()
        if (category == "자기계발" || category == "에세이" || category == "예술/대중문화") {
            category = "자기계발"
        } else if (category == "소설/시/희곡" || category == "장르소설" || category == "전집/중고전집") {
            category = "소설"
        } else if (category == "좋은부모") {
            category = "육아"
        } else if (category == "사회과학" || category == "경제경영") {
            category = "사회"
        } else if (category == "어린이" || category == "유아") {
            category = "어린이"
        } else if (category == "종교/역학" || category == "인문학" || category == "역사" || category == "고전") {
            category = "인문"
        } else if (category == "가정/요리/뷰티" || category == "건강/취미/레저" || category == "여행" || category == "잡지" || category == "달력/기타") {
            category = "생활"
        } else if (category == "외국어" || category == "대학교재" || category == "초중고참고서" || category == "수험서/자격증" || category == "공무원 수험서" || category == "컴퓨터/모바일") {
            category = "공부"
        }
        var unboxint = 0
        if (null == genre!![category]) {
            genre!![category] = 1
        } else {
            unboxint = genre!![category]!!
            unboxint++
            genre!!.replace(category, unboxint)
        }
    }


}