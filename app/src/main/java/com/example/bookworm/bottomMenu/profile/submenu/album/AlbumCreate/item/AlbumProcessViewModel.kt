package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.item

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view.CreateAlbumContentActivity
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

@SuppressLint("StaticFieldLeak")
class AlbumProcessViewModel(val context: Context, val pv: UserInfoViewModel) : ViewModel() {
    var newAlbumData: MutableLiveData<AlbumData> = MutableLiveData() //앨범 데이터 객체 생성
    var albumData = AlbumData()
    val db = FirebaseFirestore.getInstance() //파이어스토어와 연결
    var token: String? = null
    var parentActivity: CreateAlbumContentActivity


    lateinit var collectionReference: CollectionReference

    class Factory(val context: Context, val userInfoViewModel: UserInfoViewModel) :
            ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return AlbumProcessViewModel(context, userInfoViewModel) as T
        }
    }

    init {
        newAlbumData.value = albumData
        parentActivity = context as CreateAlbumContentActivity
        val job1 = viewModelScope.launch { pv.getUser(null, false) } //사용자 정보를 가져오는 작업
        val job2 = viewModelScope.launch {
            job1.join()
            token = pv.userInfoLiveData.value!!.token
        }
        viewModelScope.launch {
            job2.join()
            collectionReference = db.collection("users").document(token!!).collection("albums")
        }
    }

    fun modifyName(name: String) {
        albumData.albumName = name
    }

    fun modifyThumb(url: String) {
        albumData.thumbnail = url
    }

    //피드 리스트 수정
    fun modifyFeedList(list: ArrayList<Feed>) {
//        albumData.containsList!!.clear()
//        albumData.containsList!!.addAll(list)
    }

    //앨범데이터 업데이트
    fun updateAlbum() {
        newAlbumData.value = albumData
    }

    //앨범 이름 사용가능 여부 확인
    fun isOkayToUse(name: String) {
        if (name.equals("") || name.contains(" ")) {
            Toast.makeText(
                    context,
                    "앨범명에는 공백을 포함할 수 없습니다. \n 다시 시도해 주세요.",
                    Toast.LENGTH_SHORT
            ).show()
        } else
            viewModelScope.launch {
                var result = collectionReference.whereEqualTo("albumName", name).get().await()
                if (result.isEmpty) {
                    parentActivity.albumProcessViewModel.modifyName(name)
                    //데이터 삽입
//                    parentActivity.switchTab(1)
                } else {
                    Toast.makeText(
                            context,
                            "앨범명이 중복되었습니다. \n 다른 이름으로 시도해주세요",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //앨범 이름 사용가능 여부 확인
    suspend fun titleDuplicationCheck(name: String): Boolean {
        if (name.equals("") || name.contains(" ")) {
            Toast.makeText(
                    context,
                    "앨범명에는 공백을 포함할 수 없습니다. \n 다시 시도해 주세요.",
                    Toast.LENGTH_SHORT
            ).show()
            return false
        } else
            return withContext(viewModelScope.coroutineContext) {
                var result = collectionReference.whereEqualTo("albumName", name).get().await()
                if (result.isEmpty) {
                    parentActivity.albumProcessViewModel.modifyName(name)
                    true
                    //데이터 삽입
//                    parentActivity.switchTab(1)
                } else {
                    Toast.makeText(
                            context,
                            "앨범명이 중복되었습니다. \n 다른 이름으로 시도해주세요",
                            Toast.LENGTH_SHORT
                    ).show()
                    false
                }
            }

    }

    //서버에 앨범 업로드
    fun uploadAlbum(album: AlbumData = albumData) {
        //서버에 이미지 업로드
        viewModelScope.launch {
            album.albumId = "${System.currentTimeMillis()}_${token}"

            if (album.thumbnail != "") {
                var uploadImageToServer = viewModelScope.launch {
                    var imgName = "album_${album.albumName.hashCode()}_${token}.jpg"
//                    var url = parentActivity.imageProcessing.uploadImg(getImgBitmap()!!, imgName)
//                    modifyThumb(url)
                }
                uploadImageToServer.join()
            }
            val uploadAlbumToFB = viewModelScope.launch {
                albumData.creatorToken = token!! //업로드하는 당사자의 토큰을 담는다.
                collectionReference.document(album.albumName).set(album).await()
            }
            uploadAlbumToFB.join()
            // 앨범 생성 후 페이지 새로고침 진행을 위함.
            parentActivity.setResult(Activity.RESULT_OK)
            parentActivity.finish()


        }
    }

    private fun getImgBitmap(): Bitmap? {
        try {
            var bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                                context.contentResolver,
                                Uri.parse(albumData.thumbnail!!)
                        )
                )
            } else {
                MediaStore.Images.Media.getBitmap(
                        context.contentResolver,
                        Uri.parse(albumData.thumbnail!!)
                )
            }
            return bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}