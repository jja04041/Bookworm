package com.example.bookworm.Feed

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.example.bookworm.Feed.Comments.subactivity_comment
import com.example.bookworm.Feed.items.Feed
import com.example.bookworm.MainActivity
import com.example.bookworm.R
import com.example.bookworm.fragments.fragment_feed
import com.example.bookworm.modules.FBModule

class CustomPopup(context: Context?, anchor: View?) : PopupMenu(context, anchor), PopupMenu.OnMenuItemClickListener {
    var context: Context? = context  //종료할 액티비티의 context , ?이 붙여지면 nullable한 변수
    var context2: Context? = null //
    var fbModule: FBModule? = null
    var layout: Int = R.menu.feed_menu
    var item: Feed?=null

    fun setItems(context: Context,fbModule: FBModule,feed :Feed) {
        this.fbModule = fbModule
        this.context2=context
        this.item=feed
        this.menuInflater.inflate(layout, this.menu)
    }
    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        if (layout == R.menu.feed_menu) {
            var pos:Int=item!!.position
            var ff: fragment_feed?= (context2 as MainActivity).supportFragmentManager.findFragmentByTag("0") as fragment_feed?
            var oldList:ArrayList<Feed>?=ArrayList(ff!!.feedList)
            when (p0?.itemId) {
                R.id.menu_modify ->
                    return true
                R.id.menu_delete -> {
                    fbModule!!.deleteData(1, item!!.feedID) //삭제
                    oldList?.removeAt(pos)
                    ff.replaceItem(oldList)
                    if(context is subactivity_comment ) (context as subactivity_comment).finish()
                }
                else -> return true
            }
        }
        return false;
    }


}