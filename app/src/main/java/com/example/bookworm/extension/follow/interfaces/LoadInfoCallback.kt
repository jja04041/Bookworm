package com.example.bookworm.extension.follow.interfaces

import com.example.bookworm.core.userdata.UserInfo
import com.google.firebase.firestore.QuerySnapshot


interface LoadInfoCallback {
    fun isFollowed(info:QuerySnapshot?)
    fun onCheckedInfoLoaded(info: QuerySnapshot,userList:ArrayList<UserInfo>)
    fun onDataNotAvailable()
}
