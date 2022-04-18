package com.example.bookworm.Feed

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.PopupMenu
import com.example.bookworm.Feed.Comments.Comment
import com.example.bookworm.Feed.Comments.CommentAdapter
import com.example.bookworm.Feed.Comments.commentsCounter
import com.example.bookworm.Feed.Comments.subactivity_comment
import com.example.bookworm.Feed.items.Feed
import com.example.bookworm.MainActivity
import com.example.bookworm.R
import com.example.bookworm.fragments.fragment_feed
import com.example.bookworm.modules.FBModule

class CustomPopup(context: Context?, anchor: View?) : PopupMenu(context, anchor),
    PopupMenu.OnMenuItemClickListener {
    var context: Context? = context  //종료할 액티비티의 context , ?이 붙여지면 nullable한 변수
    var context2: Context? = null //
    var fbModule: FBModule? = null
    var layout: Int? = null
    var item: Feed? = null
    var commentitem: Comment? = null

    fun setItems(context: Context, fbModule: FBModule, feed: Feed) {
        this.fbModule = fbModule
        this.context2 = context
        this.item = feed
        this.layout = R.menu.feed_menu
        this.menuInflater.inflate(R.menu.feed_menu, this.menu) //레이아웃에 inflate
    }

    fun setItems(context: Context, fbModule: FBModule, comment: Comment, feed: Feed) {
        this.fbModule = fbModule
        this.context2 = context
        this.item = feed
        this.commentitem = comment
        this.layout = R.menu.comment_menu
        this.menuInflater.inflate(R.menu.comment_menu, this.menu) //레이아웃에 inflate
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        if (layout == R.menu.feed_menu) { //피드의 메뉴 팝업
            var pos: Int = item!!.position
            var ff: fragment_feed? =
                (context2 as MainActivity).supportFragmentManager.findFragmentByTag("0") as fragment_feed?
            var oldList: ArrayList<Feed>? = ArrayList(ff!!.feedList)
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
                    fbModule!!.deleteData(1, item!!.feedID) //삭제
                    oldList?.removeAt(pos)
                    ff.replaceItem(oldList)
                    //만약 댓글을 모아보는 액티비티(subactivity_comment)에 있는 경우, 해당 액티비티를 종료
                    if (context is subactivity_comment) (context as subactivity_comment).finish()
                }
                else -> return true
            }
        } else if (layout == R.menu.comment_menu) { //댓글의 메뉴 팝업
            var pos: Int = commentitem!!.position
            var ac: subactivity_comment? = (context as subactivity_comment)
            var oldList: ArrayList<Any> = ArrayList(ac!!.commentList)
            when (p0?.itemId) {
                R.id.menu_delete -> {
                    fbModule!!.deleteData(1, item!!.feedID, commentitem!!.commentID) //삭제
                    val data = HashMap<String, Comment>()
                    data.put("comment", commentitem!!)
                    commentsCounter().removeCounter(data, context, item!!.feedID)

                    oldList?.removeAt(pos)
                    (context as (subactivity_comment)).replaceItem(oldList)
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
        } else if (layout == R.menu.comment_menu) { //레이아웃이 댓글_메뉴 일때
            this.menu.findItem(R.id.menu_delete).setVisible(boolean)
        }
    }


}