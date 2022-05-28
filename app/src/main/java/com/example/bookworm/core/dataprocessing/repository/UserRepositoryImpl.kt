package com.example.bookworm.core.dataprocessing.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.core.userdata.UserInfo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

//사용자 정보 관련 데이터 저장소
//데이터를 외부에서 가져오는 역할만 한다.
class UserRepositoryImpl(val context: Context) : DataRepository.HandleUser {
    val db = FirebaseFirestore.getInstance() //파이어스토어와 연결
    var userPref: SharedPreferences //shareduserPreference과 연결
    var bwPref: SharedPreferences
    var collectionReference = db.collection("users")

    val gson = Gson()
    var LIMIT: Long = 5

    //초기화
    init {
        userPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        bwPref = context.getSharedPreferences("bookworm", Context.MODE_PRIVATE)
    }

    //사용자 가져오기 => token이 null인 경우 현재 사용자 데이터 가져옴
    //getFrom true: 인터넷에서 가져오기, false: 로컬에서 가져오기
    override suspend fun getUser(token: String?, getFromExt: Boolean): UserInfo? {
        //로컬에서 해당 토큰 확인
        var user = userPref.getString("key_user", null)
        var userInfo: UserInfo?

        //로컬에 유저 데이터가 있는 경우
        if (user != null) {
            val json = JSONObject(user)
            var userInfo = gson.fromJson(json.toString(), UserInfo::class.java)
            //가져오는 사용자가 현재유저인 경우, 실시간 데이터가 요구된다면, 실시간 데이터를 제공하는 메소드 구성
            //로컬에 있는 현재 유저 값을 원하는 경우
            if ((token == null || userInfo.token == token) && !getFromExt) {
                userInfo.isMainUser = true
                return userInfo
            }
            //로컬에 해당 토큰이 없는 경우 or 서버의 유저 정보를 가지고 오고 싶은 경우=>  서버에서 가져옴
            else return if (token == null) userInfo.token.let { getUserInFB(it).await() }
            else token.let { getUserInFB(it).await() }
        }

        //로컬에 아직 메인 사용자가 등록되지 않은 경우 ,서버에서 값을 가져옴.
        //=> 기존 유저가 기기 변경시 데이터가 저장되지 않는 오류 수정
        else {
            userInfo = CoroutineScope(Dispatchers.IO).async { getUserInFB(token!!).await() }.await()
            if (userInfo!=null){
                userInfo.isMainUser = true //메인 유저로 설정
                val bw = getBookWorm(userInfo.token)
                saveInLocal(userInfo, bw)  //로컬에 해당 정보 저장
                return userInfo
            }
            else return  UserInfo()
        }

    }

    override suspend fun updateBookWorm( token: String?, bookWorm: BookWorm) {
        var token = token
        if (token==null){
            token=getUser(null,false)!!.token
        }
        updateBwInFB(token!!, bookWorm)
        updateBwInLocal(bookWorm)
    }


    //사용자 정보 수정 => 개인만 가능

    override suspend fun updateUser(user: UserInfo) {
        updateInLocal(user)
        updateInFB(user)
    }

    override suspend fun getBookWorm(token: String): BookWorm {
        //로컬, 서버에서 가져오는 방법
        val key_bookworm = bwPref.getString("key_bookworm", null)
        if (key_bookworm != null) {
            val json = JSONObject(key_bookworm)
            var bookWorm = gson.fromJson(json.toString(), BookWorm::class.java)
            return bookWorm
        } else
        return CoroutineScope(Dispatchers.IO).async {
            var bookWorm = BookWorm()
            var it = collectionReference.document(token).get().await()
            var map = it.get("BookWorm") as MutableMap<Any?, Any?>?
            bookWorm.add(map)
            bookWorm
        }.await()


    }

    //사용자 생성과 동시에 파이어 베이스에 등록
    suspend override fun createUser(user: UserInfo): Boolean {
        val bookworm = BookWorm()
        return CoroutineScope(Dispatchers.IO).async {
            var result = getUserInFB(user.token).await()
            if (result != null) {
                saveInLocal(result,bookworm)
            } else {
                bookworm.token = user.token
                // get fcm token
                var a = FirebaseMessaging.getInstance().token.await()
                user.fCMtoken = a
                saveInLocal(user, bookworm)
                saveInFB(user, bookworm)
            }
            true
        }.await()
    }


    //사용자 탈퇴
    override fun deleteUser() {
        CoroutineScope(Dispatchers.IO).launch {
            //1. 현재 유저의 정보를 가져온다.
            var NowUser = getUser(null, false) //현재 사용자
            //2. 파이어 베이스의 유저정보를 제거한다.

            //3. 로컬에 저장된 유저의 정보를 제거한다.

            //4. 현재 유저의 플랫폼에 맞추어 회원 탈퇴를 진행한다.
            when (NowUser!!.platform) {
                //카카오인 경우
                "Kakao" -> {

                }
                //구글인 경우
                else -> {

                }
            }
            //5. 서버에게 해당 사용자와 관련된 데이터를 제거하도록 요청 한다.
        }
    }


//Public method


    //사용자가 현재 팔로우 중인지 확인
    suspend fun isFollowNow(user: UserInfo) = CoroutineScope(Dispatchers.IO).async {
        var localUser = getUser(null, false) //현재 유저의 정보를 가져옴
        //현재 유저의 팔로잉 목록에서 인자로 넘겨받은 유저의 토큰이 있는지 확인

        var query = collectionReference.document(localUser!!.token).collection("following")
            .whereEqualTo(FieldPath.documentId(), user.token)
        async {
            var it = query.get().await()
            !it.isEmpty //리턴 값
        }.await()
    }.await()

//Private Method

    //사용자 정보 저장
    //로컬에 저장
    private fun saveInLocal(user: UserInfo, bookworm: BookWorm?) {
        var editor = userPref.edit()
        var userInfo = gson.toJson(user, UserInfo::class.java)
        editor.putString("key_user", userInfo)
        editor.commit() //생성 완료

        //Bookworm 저장
        editor = bwPref.edit()
        val strbookworm = gson.toJson(bookworm, BookWorm::class.java)
        editor.putString("key_bookworm", strbookworm)
        editor.commit()
    }


    //파이어스토어에 저장
    private suspend fun saveInFB(user: UserInfo, bookworm: BookWorm): Boolean {
        var map = mapOf("BookWorm" to bookworm, "UserInfo" to user)
        collectionReference.document(user.token).set(map)
            .await()
        return true
    }

    //사용자 정보 업데이트
    fun updateInLocal(user: UserInfo) {
        val editor = userPref.edit()
        val userinfo = gson.toJson(user, UserInfo::class.java)
        editor.putString("key_user", userinfo)
        editor.commit()
        Log.d("nowUser", userPref.getString("key_user", null)!!)
    }

    //파이어 스토어에서 업데이트
    private suspend fun updateInFB(user: UserInfo) {
        collectionReference.document(user.token)
            .update("UserInfo", user)
            .addOnSuccessListener {
                Log.d("사용자데이터 업데이트 성공", "파이어스토어 서버에 사용자 데이터가  업데이트 되었습니다.");
            }.addOnFailureListener {
                Log.e("사용자데이터 업데이트 실패", "파이어스토어 서버에 사용자 데이터가 업데이트 되지 않았습니다.");
            }
            .await()
    }

    private fun getUserInFB(token: String) = CoroutineScope(Dispatchers.IO).async {
        //초기화 ->
        // 초기화가 안되면
        // 현재 사용자의 결과값에 일부 수정이 이루어진 채로 값이 리턴되기 때문
        var userInfo = UserInfo()
        var it = collectionReference
            .document(token!!)
            .get().await()
        try {
            var map = it.get("UserInfo") as MutableMap<String, String>
            userInfo.add(map)
            userInfo//최종 return 값
        } catch (e: FirebaseException) {
            null
        }catch (e:NullPointerException) {null}
    }

    private suspend fun updateBwInFB(token: String, bookworm: BookWorm) {
        collectionReference.document(token)
            .update("BookWorm", bookworm)
            .addOnSuccessListener {
                Log.d("책볼레 데이터  업데이트 성공", "파이어스토어 서버에 책볼레 데이터가  업데이트 되었습니다.");
            }.addOnFailureListener {
                Log.e("책볼레 데이터 업데이트 실패", "파이어스토어 서버에 책볼레 데이터 업데이트를 실패하였습니다.");
            }
            .await()
    }

    private fun updateBwInLocal(bookworm: BookWorm) {
        //Bookworm 저장
        var editor = bwPref.edit()
        val strbookworm = gson.toJson(bookworm, BookWorm::class.java)
        editor.putString("key_bookworm", strbookworm)
        editor.commit()
    }


}
