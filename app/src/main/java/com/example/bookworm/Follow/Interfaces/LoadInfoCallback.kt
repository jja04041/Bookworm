package com.example.bookworm.Follow.Interfaces

import com.example.bookworm.Core.UserData.UserInfo
import com.google.firebase.firestore.QuerySnapshot


interface LoadInfoCallback {
    fun isFollowed(info:QuerySnapshot?)
    fun onCheckedInfoLoaded(info: QuerySnapshot,userList:ArrayList<UserInfo>)
    fun onDataNotAvailable()
}
