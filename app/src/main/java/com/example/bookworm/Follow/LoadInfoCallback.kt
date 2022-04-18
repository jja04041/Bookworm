package com.example.bookworm.Follow

import com.example.bookworm.User.UserInfo
import com.google.firebase.firestore.QuerySnapshot


interface LoadInfoCallback {
//    fun onInfoLoaded(info: QuerySnapshot)
    fun isFollowed(info:QuerySnapshot)
    fun onCheckedInfoLoaded(info: QuerySnapshot,userList:ArrayList<UserInfo>)
    fun onDataNotAvailable()
}
