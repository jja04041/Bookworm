package com.example.bookworm.bottomMenu.bookworm

import com.example.bookworm.R

import kotlin.collections.ArrayList
import kotlin.collections.HashMap


//책볼레 DTO
data class BookWorm(
        var token // 사용자의 토큰
        : String = "",
        var wormType
        : String = "default",
        var wormList //책볼레 목록을 담는 리스트
        : ArrayList<String> = ArrayList(arrayListOf(wormType)),
        val bgType
        : Int = R.drawable.bg_default,
        var bgList //책볼레 목록을 담는 리스트
        : ArrayList<Int> = ArrayList(arrayListOf(bgType)),
        var readCount //읽은 도서의 수
        : Int = 0,
        var readBook //읽은 책 번호 목록
        : ArrayList<String> = ArrayList(),
        var achievementMap
        : HashMap<String, Boolean> = HashMap(),
)
