package com.example.bookworm.Follow

import com.example.bookworm.User.UserInfo

/*
********************************************
MVP 디자인 패턴을 이용하여 구현하였다.
* 참고: https://salix97.tistory.com/205
********************************************
*/

//전반적인 인터페이스

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
