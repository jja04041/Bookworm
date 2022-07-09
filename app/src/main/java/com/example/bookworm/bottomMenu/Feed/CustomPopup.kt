package com.example.bookworm.Feed

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.example.bookworm.R
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.Feed.Fragment_feed
import com.example.bookworm.bottomMenu.Feed.comments.Comment
import com.example.bookworm.bottomMenu.Feed.comments.CommentsCounter
import com.example.bookworm.bottomMenu.Feed.comments.subactivity_comment
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.Feed.subActivity_Feed_Modify
import com.example.bookworm.bottomMenu.challenge.board.*
import com.example.bookworm.core.internet.FBModule

class CustomPopup(context: Context?, anchor: View?) : PopupMenu(context, anchor),
    PopupMenu.OnMenuItemClickListener {
    var context: Context? = context  //종료할 액티비티의 context , ?이 붙여지면 nullable한 변수
    var context2: Context? = null //
    var fbModule: FBModule? = null
    var layout: Int? = null
    var item: Feed? = null
    var boardItem: Board? = null
    var commentitem: Comment? = null
    var boardFB: BoardFB? = null

    fun setItems(context: Context, fbModule: FBModule, feed: Feed) { //피드탭
        this.fbModule = fbModule
        this.context2 = context
        this.item = feed
        this.layout = R.menu.feed_menu
        this.menuInflater.inflate(R.menu.feed_menu, this.menu) //레이아웃에 inflate
    }

    fun setItems(context: Context, boardFB: BoardFB, board: Board) { //인증글 댓글화면 (인증글)
        this.boardFB = boardFB
        this.context2 = context
        this.boardItem = board
        this.layout = R.menu.board_menu
        this.menuInflater.inflate(R.menu.board_menu, this.menu) //레이아웃에 inflate
    }

    fun setItems(context: Context, fbModule: FBModule, comment: Comment, feed: Feed) { //피드 댓글화면
        this.fbModule = fbModule
        this.context2 = context
        this.item = feed
        this.commentitem = comment
        this.layout = R.menu.comment_menu
        this.menuInflater.inflate(R.menu.comment_menu, this.menu) //레이아웃에 inflate
    }

    fun setItems(
        context: Context,
        boardFB: BoardFB,
        comment: Comment,
        board: Board
    ) { //인증글 댓글화면 (댓글)
        this.boardFB = boardFB
        this.context2 = context
        this.boardItem = board
        this.commentitem = comment
        this.layout = R.menu.board_comment_menu
        this.menuInflater.inflate(R.menu.board_comment_menu, this.menu) //레이아웃에 inflate
    }


    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        if (layout == R.menu.feed_menu) { //피드의 메뉴 팝업
            var pos: Int = item!!.position
            var ff: Fragment_feed? =
                (context2 as MainActivity).supportFragmentManager.findFragmentByTag("0") as Fragment_feed?
            var oldList: ArrayList<Feed?> = ArrayList()
            oldList!!.addAll(ff!!.feedList)
            when (p0?.itemId) {
                R.id.menu_modify -> {
                    //현재 화면이 댓글 화면이라면
                    if (context is subactivity_comment) {
                        var intent: Intent? = Intent(context, subActivity_Feed_Modify::class.java)
                        intent?.putExtra("Feed", item)
                        (context as (subactivity_comment)).startActivityResult.launch(intent)
                    }
                    //현재 화면이 피드 화면이라면
                    else {
                        var intent: Intent? = Intent(context, subActivity_Feed_Modify::class.java)
                        intent?.putExtra("Feed", item)
                        ff.startActivityResult.launch(intent)//이렇게 하면 피드에서 값 세팅은 될지 모르겠지만,,,

                    }
                    return true
                }
                R.id.menu_delete -> {
                    AlertDialog.Builder(context)
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("네") { dialog: DialogInterface, which: Int ->
                            dialog.dismiss()
                            fbModule!!.deleteData(1, item!!.feedID) //삭제
                            oldList?.removeAt(pos)
                            ff.feedAdapter!!.submitList(oldList)
                            //만약 댓글을 모아보는 액티비티(subactivity_comment)에 있는 경우, 해당 액티비티를 종료
                            if (context is subactivity_comment) (context as subactivity_comment).finish()
                        }
                        .setNegativeButton("아니요") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                        .show()
                }
                else -> return true
            }
        } else if (layout == R.menu.comment_menu) { //댓글의 메뉴 팝업
            var pos: Int = commentitem!!.position
            var ac: subactivity_comment? = (context as subactivity_comment)
            var oldList: ArrayList<Any> = ArrayList(ac!!.commentList)
            when (p0?.itemId) {
                R.id.menu_delete -> {
                    AlertDialog.Builder(context)
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("네") { dialog: DialogInterface, which: Int ->
                            dialog.dismiss()
                            fbModule!!.deleteData(1, item!!.feedID, commentitem!!.commentID) //삭제
                            val data = HashMap<String, Comment>()
                            data.put("comment", commentitem!!)
                            CommentsCounter()
                                .removeCounter(data, context, item!!.feedID)

                            oldList?.removeAt(pos)
                            (context as (subactivity_comment)).replaceItem(oldList)
                        }
                        .setNegativeButton("아니요") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                        .show()
                }
                else -> return true
            }
        } else if (layout == R.menu.board_comment_menu) { //인증글 댓글의 메뉴 팝업
            var pos: Int = commentitem!!.position
            var ac: subactivity_challenge_board_comment? =
                (context as subactivity_challenge_board_comment)
            var oldList: ArrayList<Any> = ArrayList(ac!!.commentList)
            when (p0?.itemId) {
                R.id.menu_delete -> {
                    AlertDialog.Builder(context)
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("네") { dialog: DialogInterface, which: Int ->
                            dialog.dismiss()
                            boardFB!!.deleteComment(boardItem, commentitem!!.commentID) //삭제
                            val data = HashMap<String, Comment>()
                            data.put("comment", commentitem!!)
                            Board_CommentsCounter()
                                .removeCounter(
                                    data,
                                    context,
                                    boardItem!!.challengeName,
                                    boardItem!!.boardID
                                )

                            oldList?.removeAt(pos)
                            (context as (subactivity_challenge_board_comment)).replaceItem(oldList)
                        }
                        .setNegativeButton("아니요") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                        .show()
                }
                else -> return true
            }
        } else if (layout == R.menu.board_menu) { //인증글의 메뉴 팝업
            when (p0?.itemId) {
                R.id.menu_allow -> { //인증글 승인시 (챌린지 개설자 권한)

                }
                R.id.menu_notallow -> { //인증글 반려시 (챌린지 개설자 권한)

                }
                R.id.menu_delete -> { //본인이 인증글 삭제시
                    AlertDialog.Builder(context)
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("네") { dialog: DialogInterface, which: Int ->
                            dialog.dismiss()
                            boardFB!!.deleteBoard(boardItem) //인증글 삭제
                            (context as subactivity_challenge_board_comment).finish() //액티비티 종료
                        }
                        .setNegativeButton("아니요") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                        .show()
                }
                else -> return true
            }
        }
        return false;
    }

    fun setVisible(boolean: Boolean) {
        if (layout == R.menu.feed_menu) { //레이아웃이 피드_메뉴 일때
            this.menu.findItem(R.id.menu_delete).setVisible(boolean)
            this.menu.findItem(R.id.menu_modify).setVisible(boolean)
        }
        else if (layout == R.menu.comment_menu) { //레이아웃이 댓글_메뉴 일때
            this.menu.findItem(R.id.menu_delete).setVisible(boolean)
        }
        else if (layout == R.menu.board_menu) { //레이아웃이 인증글_메뉴 일때
            this.menu.findItem(R.id.menu_allow).setVisible(boolean)
            this.menu.findItem(R.id.menu_notallow).setVisible(boolean)
        }
        else if (layout == R.menu.board_comment_menu) { //레이아웃이 인증글_댓글_메뉴 일때
            this.menu.findItem(R.id.menu_delete).setVisible(boolean)
        }
    }

    fun  setDeleteVisible(boolean: Boolean) { //작성자만 자신의 인증글을 삭제할 수 있게 함
        if (layout == R.menu.board_menu) { //레이아웃이 인증글_메뉴 일때
            this.menu.findItem(R.id.menu_delete).setVisible(boolean)
        }
    }


}