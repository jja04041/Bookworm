package com.example.bookworm.bottomMenu.profile.Album.item

import com.example.bookworm.bottomMenu.Feed.items.Feed

//앨범 객체

class AlbumData {
    var thumbnail:String?=null //앨범 커버
    var albumName:String?=null //앨범 이름
    var containsList:ArrayList<Feed>?=null //앨범이 가진 피드 리스트

    //파이어베이스에서 전달받은 데이터를 객체에 담음
    fun addData(data:Map<String,Any>){
        thumbnail= data["thumnail"] as String?
        albumName = data["albumName"] as String?
        containsList = data["containsList"] as ArrayList<Feed>
    }
}