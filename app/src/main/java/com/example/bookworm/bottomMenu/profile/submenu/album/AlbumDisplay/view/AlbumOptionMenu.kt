package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view.CreateAlbumContentActivity
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


//앨범의 옵션 메뉴를 보여주는 부분
class AlbumOptionMenu(context: Context?, anchor: View?, val data: AlbumData) :
    PopupMenu(context, anchor),
    PopupMenu.OnMenuItemClickListener {
    var context: Context? = context  //종료할 액티비티의 context , ?이 붙여지면 nullable한 변수

    init {
        this.menuInflater.inflate(R.menu.feed_menu, this.menu) //메뉴 레이아웃에 메뉴 아이템 레이아웃을 삽입한다.
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        when (p0!!.itemId) {
            //앨범 수정
            R.id.menu_modify -> {
                var intent = Intent(context, CreateAlbumContentActivity::class.java)
                intent.putExtra("albumData", data) //기존의 앨범 데이터를 수정할 수 있는 액티비티로 넘긴다.
                (context as ShowAlbumContentActivity).startActivity(intent) // 앨범을 수정할 수 있는 액티비티를 실행한다.
                return false
            }
            //앨범 삭제
            else -> {
                AlertDialog.Builder(context)
                    .setTitle("앨범 삭제")
                    .setMessage("앨범을 삭제하시겠습니까?")
                    .setPositiveButton("예"
                    ) { p0, p1 ->
                        //예를 선택할 때 적용할 메커니즘
                        //서버에서 앨범을 삭제하는 작업을 거친다.
                        CoroutineScope(Dispatchers.IO).launch {
                            FirebaseFirestore.getInstance().collection("users")
                                    .document(data.creatorToken!!)
                                    .collection("albums").document(data.albumName!!).delete()
                        }
                        (context as ShowAlbumContentActivity).finish()
                    }
                        .setNegativeButton("아니오",
                        object : DialogInterface.OnClickListener {
                            //아니오를 선택할 때 적용할 메커니즘
                            override fun onClick(p0: DialogInterface?, p1: Int) {

                            }
                        })
                    .show() // 작성된 Dialog를 화면에 표시한다.
                return false
            }
        }
    }
}