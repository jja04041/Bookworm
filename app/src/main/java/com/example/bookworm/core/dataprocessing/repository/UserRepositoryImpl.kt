package com.example.bookworm.core.dataprocessing.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.core.userdata.UserInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.gson.Gson
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
    var bwPref:SharedPreferences
    var collectionReference = db.collection("users")

    val gson = Gson()
    var LIMIT: Long = 5

    //초기화
    init {
        userPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        bwPref= context.getSharedPreferences("bookworm", Context.MODE_PRIVATE)
    }

    //사용자 가져오기 => token이 null인 경우 현재 사용자 데이터 가져옴
    suspend override fun getUser(token: String?,isFirst: Boolean): UserInfo? {
        //로컬에서 해당 토큰 확인
        var user = userPref.getString("key_user", null)
        val json = JSONObject(user)
        var userInfo = gson.fromJson(json.toString(), UserInfo::class.java)

        //현재 유저인 경우 바로 유저인포 넘겨줌
        if (token == null || userInfo.token == token && !isFirst) {
            userInfo.isMainUser = true
            return userInfo
        }
        //로컬에 해당 토큰이 없는 경우, 서버에서 가져옴
        else
            return try {
                //초기화 ->
                // 초기화가 안되면
                // 현재 사용자의 결과값에 일부 수정이 이루어진 채로 값이 리턴되기 때문
                userInfo = UserInfo()
                var it = collectionReference
                    .document(token!!)
                    .get().await()
                var map = it.get("UserInfo") as MutableMap<String, String>
                userInfo.add(map)
                userInfo //최종 return 값
            } catch (e: FirebaseException) {
                null
            }
    }


    //사용자 정보 수정 => 개인만 가능
    override fun updateUser(user: UserInfo) {
        updateInLocal(user)
    }

    suspend fun updateBoth(user: UserInfo) {
        updateInLocal(user)
        updateInFB(user)
    }


    //사용자 생성과 동시에 파이어 베이스에 등록
    suspend override fun createUser(user: UserInfo):Boolean {
        val bookworm = BookWorm()
        bookworm.token = user.token
        saveInLocal(user,bookworm)
        return saveInFB(user,bookworm)
    }


    //사용자 탈퇴
    override fun deleteUser() {

    }

//Public method

    //사용자의 팔로워/팔로잉 목록의 토큰 리스트를 가져옴
    override suspend fun getFollowTokenList(
        token: String,
        check: Int,
        lastVisible: String?
    ) = CoroutineScope(Dispatchers.IO).async {
        var tokenList = ArrayList<String>()
        launch {
            val type = if (check == 1) "follower" else "following"
            var query = collectionReference.document(token).collection(type).orderBy("token")
            if (lastVisible != null) query = query.startAfter(lastVisible)
            var it = query.limit(10).get().await()
            for (i in it.documents) tokenList.add(i.id)
        }
        tokenList
    }.await()

    //사용자가 현재 팔로우 중인지 확인
    suspend fun isFollowNow(user: UserInfo) = CoroutineScope(Dispatchers.IO).async {
        var localUser = getUser(null,false) //현재 유저의 정보를 가져옴
        //현재 유저의 팔로잉 목록에서 인자로 넘겨받은 유저의 토큰이 있는지 확인
        var query = collectionReference.document(localUser!!.token).collection("following")
            .whereEqualTo(FieldPath.documentId(), user.token)
        async {
            var it = query.get().await()
            !it.isEmpty //리턴 값
        }.await()
    }.await()

    //팔로우 처리
    fun followProcessing(
        fromUserInfo: UserInfo,
        toUserInfo: UserInfo,
        type: Boolean
    ): Task<Transaction> {
        val fromRef = collectionReference.document(fromUserInfo.token)
        val toRef = collectionReference.document(toUserInfo.token)
        val fromRefFollow = fromRef.collection("following").document(toUserInfo.token)
        val toRefFollow = toRef.collection("follower").document(fromUserInfo.token)


        return db.runTransaction {
            var current = it.get(fromRef).getLong("UserInfo.followingCounts")
            var count = if (type) 1 else -1.toLong()
            current = current?.plus(count)
            it.update(fromRef, "UserInfo.followingCounts", current)
                .update(toRef, "UserInfo.followerCounts", FieldValue.increment(count))
            fromUserInfo.followingCounts = current!!.toInt()
            updateInLocal(fromUserInfo)//새로 갱신된 데이터를 로컬과 서버 모두에 적용
            if (type) {
                it.set(fromRefFollow, toUserInfo).set(toRefFollow, fromUserInfo)
            } else {
                it.delete(fromRefFollow)
                    .delete(toRefFollow)
            }
        }
    }
//Private Method

    //사용자 정보 저장
    //로컬에 저장
    private fun saveInLocal(user: UserInfo,bookworm: BookWorm) {
        var editor = userPref.edit()
        var userInfo = gson.toJson(user, UserInfo::class.java)
        editor.putString("key_user", userInfo)
        editor.apply() //생성 완료

        //Bookworm 저장
        editor = bwPref.edit()
        val strbookworm = gson.toJson(bookworm, BookWorm::class.java)
        editor.putString("key_bookworm", strbookworm)
        editor.commit()
    }



    //파이어스토어에 저장
    private suspend fun saveInFB(user: UserInfo,bookworm:BookWorm) : Boolean{
        var map = mapOf("BookWorm" to bookworm,"UserInfo" to user)
        collectionReference.document(user.token).set(map)
            .await()
        return true
    }

    //사용자 정보 업데이트
    private fun updateInLocal(user: UserInfo) {
        val editor = userPref.edit()
        val userinfo = gson.toJson(user, UserInfo::class.java)
        editor.putString("key_user", userinfo)
        editor.apply()
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

}
