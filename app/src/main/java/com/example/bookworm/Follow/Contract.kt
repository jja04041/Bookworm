package com.example.bookworm.Follow

import com.example.bookworm.User.UserInfo

//View와 Presenter에서 어떻게 분담하고 나눌지 미리 계획
interface Contract {
    interface View{
        //imageView와 textView에 사용자의 info를 보여준다
        fun showInfo(info:  ArrayList<UserInfo>?)
    }

    interface Presenter{
        //imageView와 textView에 사용자의 info를 보여주도록 View에게 지시한다
       fun setInfo(info: ArrayList<UserInfo>?)
    }
}
