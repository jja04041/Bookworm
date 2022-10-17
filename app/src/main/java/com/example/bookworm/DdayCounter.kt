package com.example.bookworm

import android.text.TextUtils.substring
import android.util.Log
import java.lang.Exception
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/** 디데이를 설정해주는 모듈
 * */

object DdayCounter {
    private val todaCal = Calendar.getInstance()
    private val ddayCal = Calendar.getInstance()
    fun getDdayByDash(stdDate: String) =
            try {
                stdDate.apply {
                    ddayCal.set(
                            substring(0 until 4).toInt() //Year
                            , substring(5 until 7).toInt() - 1 //Month
                            , substring(8 until 10).toInt() //Day
                    )
                }
                val count = (ddayCal.timeInMillis - todaCal.timeInMillis) / 86400000
                if (count < 0)
                    "종료됨"
                else "${count}일 남음"
            } catch (e: Exception) {
                Log.e("에러", "Error in GetDday at DdayCounter")
                ""
            }

    fun getDuration(createdTime: String): String {
        var dateDuration = ""
        val now = System.currentTimeMillis()
        val dateNow = Date(now) //현재시각
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        try {
            val dateCreated = createdTime?.let { dateFormat.parse(it) }
            val duration = dateNow.time - dateCreated!!.time //시간차이 mills
            dateDuration = if (duration / 1000 / 60 == 0L) {
                "방금"
            } else if (duration / 1000 / 60 <= 59) {
                (duration / 1000 / 60).toString() + "분 전"
            } else if (duration / 1000 / 60 / 60 <= 23) {
                (duration / 1000 / 60 / 60).toString() + "시간 전"
            } else if (duration / 1000 / 60 / 60 / 24 <= 29) {
                (duration / 1000 / 60 / 60 / 24).toString() + "일 전"
            } else if (duration / 1000 / 60 / 60 / 24 / 30 <= 12) {
                (duration / 1000 / 60 / 60 / 24 / 30).toString() + "개월 전"
            } else {
//                (duration / 1000 / 60 / 60 / 24 / 30 / 12).toString() + "년 전"
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(duration)
            }
            return dateDuration
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

}