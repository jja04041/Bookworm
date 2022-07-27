package com.example.bookworm.bottomMenu.profile.submenu.album

import com.example.bookworm.bottomMenu.Feed.items.Feed
import java.io.Serializable

//앨범 객체

class AlbumData : Serializable {
    var thumbnail: String? = null //앨범 커버
    var albumName: String? = null //앨범 이름
    var containsList= ArrayList<Feed>() //앨범이 가진 피드 리스트
    var albumId: String? = null
    var creater: String?= null

    //파이어베이스에서 전달받은 데이터를 객체에 담음
    fun addData(data: Map<String, Any>) {
        thumbnail = data["thumbnail"] as String?
        albumName = data["albumName"] as String?
        albumId = data["albumId"] as String?
        if("creater" in data) creater = data["creater"] as String?

        for(i in data["containsList"] as ArrayList<HashMap<*,*>>){
            var item = Feed()
            item.setFeedData(i)
            containsList.add(item)
        }
    }
}