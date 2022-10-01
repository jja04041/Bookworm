package com.example.bookworm

import android.util.Log
import java.lang.Exception
import java.util.*

/** 디데이를 설정해주는 모듈
 * */
class DdayCounter(stdDate: String) {
    private val todaCal = Calendar.getInstance()
    private val ddayCal = Calendar.getInstance()

    /**D-## 표시*/
    val dDayByDash =
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
                else "D-${count}"
            } catch (e: Exception) {
                Log.e("에러", "Error in GetDday at DdayCounter")
                ""
            }

}