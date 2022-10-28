package com.example.bookworm.core.dataprocessing.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.bottomMenu.feed.FireStoreLoadModule
import com.example.bookworm.bottomMenu.feed.SubActivityCreatePost
import com.example.bookworm.bottomMenu.feed.comments.SubActivityComment
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchMainActivity
import com.example.bookworm.core.userdata.UserInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

//사용자 정보 관련 데이터 저장소
//데이터를 외부에서 가져오는 역할만 한다.
class UserRepository(context: Context) : DataRepository.HandleUser {
    var userPref: SharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE) //shareduserPreference과 연결
    var bwPref: SharedPreferences = context.getSharedPreferences("bookworm", Context.MODE_PRIVATE)
    var userCollectionRef = FireStoreLoadModule.provideQueryPathToUserCollection()
    var bwCollectionRef = FireStoreLoadModule.provideQueryPathToBwCollection()
    val gson = Gson()

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
            else return token?.let {
                var data = getUserInFB(it).await()
                if (data != null && data.token == userInfo.token) data.isMainUser = true
                if (data == null) data = UserInfo()
                data
            }
                    ?: userInfo.token.let {
                        var data = getUserInFB(it).await()
                        updateInLocal(data!!)
                        data!!.isMainUser = true
                        data
                    }
        }

        //로컬에 아직 메인 사용자가 등록되지 않은 경우 ,서버에서 값을 가져옴.
        //=> 기존 유저가 기기 변경시 데이터가 저장되지 않는 오류 수정
        else {
            return try {
                userInfo = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) { getUserInFB(token!!).await() }
                userInfo!!.isMainUser = true //메인 유저로 설정
                val bw = getBookWorm(userInfo.token)
                saveInLocal(userInfo, bw)  //로컬에 해당 정보 저장
                userInfo
            } catch (e: java.lang.NullPointerException) {
                UserInfo()
            }
        }

    }

    override suspend fun updateBookWorm(token: String?, bookWorm: BookWorm) {
        updateBwInFB(
                token ?: getUser(null, false)!!.token,
                bookWorm)
        updateBwInLocal(bookWorm)
    }

    override suspend fun getAlbums(token: String?): ArrayList<AlbumData> {
        var token = token
        var resultArray = ArrayList<AlbumData>()
        return CoroutineScope(Dispatchers.IO).async {
            if (token == null) token = CoroutineScope(Dispatchers.IO).async {
                getUser(token, false)
            }.await()!!.token
            var albumReference =
                    userCollectionRef.document(token!!).collection("albums").orderBy("albumId", Query.Direction.DESCENDING).get().await()
            for (i in albumReference.documents) {
                resultArray.add(i.toObject(AlbumData::class.java)!!)
            }
            resultArray
        }.await()


    }

    override suspend fun updateAlbums(token: String?) {
        TODO("Not yet implemented")
    }


    //사용자 생성과 동시에 파이어 베이스에 등록
    override suspend fun createUser(user: UserInfo): Boolean {
        val bookworm = BookWorm()
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            bookworm.token = user.token
            // get fcm token
            var a = FirebaseMessaging.getInstance().token.await()
            user.fCMtoken = a
            saveInLocal(user, bookworm) //로컬에 저장
            saveInFB(user, bookworm)
        }
    }
    //사용자 정보 수정 => 개인만 가능

    override suspend fun updateUser(user: UserInfo) {
        updateInLocal(user)
        updateInFB(user)
    }

    //사용자 탈퇴
    override suspend fun deleteUser() {

        //1. 현재 유저의 정보를 가져온다.
        var nowUser = getUser(null, false) //현재 사용자
        //2. 파이어 베이스의 유저정보를 제거한다.
        try {
            FireStoreLoadModule.provideFirebaseInstance().runTransaction { transaction ->
                transaction.delete(userCollectionRef.document(nowUser!!.token))
                        .delete(bwCollectionRef.document(nowUser.token))
            }.await()
            //3. 로컬에 저장된 유저의 정보를 제거한다.
            logOut()
        } catch (e: FirebaseFirestoreException) {
            Log.e("파이어베이스에서 유저 삭제 실패", "실패")
        }
        //4. 서버에게 해당 사용자와 관련된 데이터를 제거하도록 요청 한다.

    }

    override suspend fun getBookWorm(token: String): BookWorm {
        //로컬, 서버에서 가져오는 방법
        val keyBookWorm = bwPref.getString("key_bookworm", null)
        return if (keyBookWorm != null) {
            val json = JSONObject(keyBookWorm)
            var bookWorm = gson.fromJson(json.toString(), BookWorm::class.java)
            if (bookWorm.token != token) getBwFromFB(token)!!
            else bookWorm
        } else getBwFromFB(token)!!


    }

    suspend fun getBwFromFB(token: String) = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        var data = bwCollectionRef.document(token).get().await()
        data.toObject(BookWorm::class.java)
    }


    //사용자 로그아웃(로컬에서)
    fun logOut() {
        var editor = userPref.edit()
        editor.remove("key_user")
        editor.apply()
        editor = bwPref.edit()
        editor.remove("key_bookworm")
        editor.apply()
    }


//Public method


    //사용자가 현재 팔로우 중인지 확인
    suspend fun isFollowNow(user: UserInfo) = CoroutineScope(Dispatchers.IO).async {
        var localUser = getUser(null, false) //현재 유저의 정보를 가져옴
        //현재 유저의 팔로잉 목록에서 인자로 넘겨받은 유저의 토큰이 있는지 확인

        var query = userCollectionRef.document(localUser!!.token).collection("following")
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
        return try {
            FireStoreLoadModule.provideFirebaseInstance().runTransaction { transaction ->
                transaction.set(userCollectionRef.document(user.token), user)
                        .set(bwCollectionRef.document(user.token), bookworm)
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    //사용자 정보 업데이트
    fun updateInLocal(user: UserInfo) {
        val editor = userPref.edit()
        val userinfo = gson.toJson(user, UserInfo::class.java)
        editor.putString("key_user", userinfo)
        editor.commit()
    }

    //파이어 스토어에서 업데이트
    private suspend fun updateInFB(user: UserInfo) {
        userCollectionRef
                .document(user.token)
                .set(user)
                .addOnSuccessListener {
                    Log.d("사용자데이터 업데이트 성공", "파이어스토어 서버에 사용자 데이터가  업데이트 되었습니다.");
                }.addOnFailureListener {
                    Log.e("사용자데이터 업데이트 실패", "파이어스토어 서버에 사용자 데이터가 업데이트 되지 않았습니다.");
                }
                .await()
    }

    //FireStore에서 유저 데이터를 가져옴.
    private fun getUserInFB(token: String) = CoroutineScope(Dispatchers.IO).async {
        //초기화 ->
        // 초기화가 안되면
        // 현재 사용자의 결과값에 일부 수정이 이루어진 채로 값이 리턴되기 때문
        var data = userCollectionRef
                .document(token!!)
                .get().await()
        try {
            data.toObject(UserInfo::class.java) //최종 return 값
        } catch (e: FirebaseException) {
            null
        } catch (e: NullPointerException) {
            null
        }
    }

    private suspend fun updateBwInFB(token: String, bookworm: BookWorm) {
        bwCollectionRef.document(token)
                .set(bookworm)
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
        val bwString = gson.toJson(bookworm, BookWorm::class.java)
        editor.putString("key_bookworm", bwString)
        editor.apply()
    }


    //팔로우 관련 함수
    //type True: 팔로우 / False: 팔로우 해제
    suspend fun follow(toUserInfo: UserInfo, type: Boolean): UserInfo {
        val fromUserInfo = CoroutineScope(Dispatchers.IO).async {
            getUser(null, false)
        }.await()!!//현재 사용자
        followProcessing(fromUserInfo, toUserInfo, type).await()//팔로우 처리
        //반환할 값(업데이트된 값)
        val returnValue = CoroutineScope(Dispatchers.IO).async {
            getUser(toUserInfo.token, true)
        }.await()
        updateInLocal(getUser(null, true)!!) //업데이트 된 사용자 정보를 로컬에도 반영
        return returnValue!!
    }

    //팔로우 처리
    fun followProcessing(
            fromUserInfo: UserInfo,
            toUserInfo: UserInfo,
            type: Boolean,
    ): Task<Transaction> {
        val userCollectionRef = FireStoreLoadModule.provideQueryPathToUserCollection()
        val fromRef = userCollectionRef.document(fromUserInfo.token)
        val toRef = userCollectionRef.document(toUserInfo.token)
        val fromRefFollow = fromRef.collection("following").document(toUserInfo.token)
        val toRefFollow = toRef.collection("follower").document(fromUserInfo.token)

        return FireStoreLoadModule.provideFirebaseInstance().runTransaction { trans ->
            trans.update(fromRef, "followingCounts", FieldValue.increment(if (type) 1L else -1L))
                    .update(toRef, "followerCounts", FieldValue.increment(if (type) 1L else -1L))
                    .apply {
                        if (type) set(fromRefFollow, toUserInfo)
                                .set(toRefFollow, fromUserInfo)
                        else delete(fromRefFollow).delete(toRefFollow)
                    }
        }
    }
}
