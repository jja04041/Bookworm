package com.example.bookworm.bottomMenu.profile.Album.item

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.profile.Album.item.AlbumData

class albumViewModel : ViewModel() {
    var newAlbumData: MutableLiveData<AlbumData> = MutableLiveData() //앨범 데이터 객체 생성
    var albumData = AlbumData()
    init {
        newAlbumData.value = albumData
    }

    fun modifyName(name: String) {
        albumData.albumName=name
    }

    fun modifyThumb(url: String) {
        albumData.thumbnail=url
    }
    fun modifyFeedList(list:ArrayList<Feed>){
        albumData.containsList!!.clear()
        albumData.containsList!!.addAll(list)
    }
    fun updateAlbum(){
        newAlbumData.value=albumData
    }

}