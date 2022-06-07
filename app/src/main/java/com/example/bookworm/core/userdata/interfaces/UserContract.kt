package com.example.bookworm.core.userdata.interfaces

import com.example.bookworm.core.userdata.UserInfo
import com.google.firebase.firestore.DocumentSnapshot


//계약 작성

//사용자 토큰을 Presenter가 받은 후,
//Presenter가 서버와 통신하여 , 유저의 UserInfo를 받아온다.
//받아온 UserInfo를 이용하여, 뷰에게 이 데이터를 보여주도록 한다.
//뷰는 전달된 값을 화면에 표시한다.

interface UserContract {
    interface View {
        fun showProfile(userInfo: UserInfo?, boolean: Boolean?)
    }

    interface Presenter {
        fun getData(token: String, boolean: Boolean?)
        fun setProfile(document: DocumentSnapshot)
    }
}