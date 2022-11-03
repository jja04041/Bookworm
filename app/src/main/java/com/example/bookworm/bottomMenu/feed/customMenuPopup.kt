package com.example.bookworm.bottomMenu.feed

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.LoadState
import com.example.bookworm.R
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.example.bookworm.bottomMenu.feed.comments.SubActivityComment

//메뉴 팝업

class customMenuPopup(val context: Context, anchor: View) : PopupMenu(context, anchor) {

    var liveState = MutableLiveData<Int>()

    //피드 관련 메뉴 변수
    val FEED_DELETE = 299 // 삭제
    val FEED_MODIFY = 1 // 수정

    val COMMENT_DELETE = 298 // 댓글 삭제

    //피드 메뉴인 경우
    fun setItem(data: Any) {
        this.menuInflater.inflate(
                when (data) {
                    is Feed -> R.menu.feed_menu
                    else -> R.menu.comment_menu
                }, this.menu)
        this.setOnMenuItemClickListener(OnMenuItemClickListener(data, context))
        //리스너 설정
        this.show() //팝업 메뉴 설정
    }

    //리스너 내부 클래스
    inner class OnMenuItemClickListener(val data: Any, val context: Context)
        : PopupMenu.OnMenuItemClickListener {
        lateinit var vm: ViewModel //뷰모델 설정


        //클릭 시
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when (item!!.itemId) {
                R.id.menu_modify -> modifyData(data)
                R.id.menu_delete -> {
                    if (data is Feed) createAlertBuilder(FEED_DELETE)
                    if (data is Comment) createAlertBuilder(COMMENT_DELETE)
                }
                else -> return true
            }
            return false
        }

        //수정 메소드
        fun modifyData(item: Any) {
            if (item is Feed) {
                //수정을 담당하는 액티비티로 이동해야함.
                when (context) {
                    is MainActivity -> {
                        val intent = Intent(context, SubActivityModifyPost::class.java)
                        intent.putExtra("Feed", item)
                        (context.supportFragmentManager.findFragmentByTag("0") as FragmentFeed).startActivityResult.launch(intent)
                    }
                    is SubActivityComment -> {
                        val intent = Intent(context, SubActivityModifyPost::class.java)
                        intent.putExtra("Feed", item)
                        context.startActivityResult.launch(intent)
                    }
                }
            }
        }

        // 알림 창 만들어주는 함수
        fun createAlertBuilder(code: Int) =
                AlertDialog.Builder(context)
                        .setMessage(
                                when (code) {
                                    FEED_DELETE -> "리뷰를 삭제하시겠습니까?" //피드(게시물) 삭제
                                    COMMENT_DELETE -> "댓글을 삭제하시겠습니까?" //댓글 삭제
                                    else -> ""
                                })
                        //참인 경우
                        .setPositiveButton("네") { dialog: DialogInterface, which: Int ->
                            dialog.dismiss() //창 닫기
                            //피드 삭제
                            vm = ViewModelProvider(if (context is MainActivity) context
                            else context as SubActivityComment)[FeedViewModel::class.java]
                            if (code == FEED_DELETE) {
                                //뷰모델 생성
//                                vm = ViewModelProvider(if (context is MainActivity) context
//                                else context as SubActivityComment)[FeedViewModel::class.java]

                                //뷰모델을 이용하여 서버에서 피드 데이터 삭제 진행
                                val state = MutableLiveData<LoadState>()
                                (vm as FeedViewModel).deletePost(data as Feed, state)
                                //어댑터 Refresh
                                //삭제된 내용을 현재 액티비티에 반영해야함.
                                state.observe(context as AppCompatActivity) {
                                    states->
                                    if(states == LoadState.Done){
                                        if (context is SubActivityComment) context.finish()
                                        else liveState.value = FEED_DELETE
                                    }
                                }
                            } else if (code == COMMENT_DELETE) { //댓글 삭제 진행
                                val state = MutableLiveData<LoadState>()
                                (vm as FeedViewModel).manageComment(data as Comment, data.feedID, false, state)
                                state.observe(context as SubActivityComment){
                                    if (state.value == LoadState.Done) liveState.value = COMMENT_DELETE
                                }
                            }
                        }.setNegativeButton("아니오") { dialog: DialogInterface, which: Int ->
                            dialog.dismiss()
                        }.show()
    }
}




