package com.example.bookworm.bottomMenu.feed

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bookworm.bottomMenu.search.items.Book
import com.example.bookworm.bottomMenu.search.subactivity.search_fragment_subActivity_main
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.SubactivityCreatePostBinding


//기존 피드 작성 액티비티에서 난잡하게 꼬여 있던 코드들을 MVVM 패턴에 맞게 재구성
//라벨 추가 및 수정 부분을 추가해야 함 .


class SubActivityCreatePost : AppCompatActivity() {
    private val dataBinding by lazy {
        SubactivityCreatePostBinding.inflate(layoutInflater)
    }
    private var feedData = Feed()
    private var feedImageBitmap: Bitmap? = null
    private val imageProcess by lazy {
        ImageProcessing(this)
    }
    private val feedViewModel by lazy {
        ViewModelProvider(this, FeedViewModel.Factory(this))[FeedViewModel::class.java]
    }


    private val mainUser by lazy {
        intent.getSerializableExtra("mainUser") as UserInfo
    }

    //액티비티 간 데이터 전달 핸들러(검색한 데이터의 값을 전달받는 매개체가 된다.) [책 데이터 이동]
    var bookResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            feedData.book = intent!!.getSerializableExtra("data") as Book?
            dataBinding.tvFeedBookTitle.text = feedData.book!!.title
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dataBinding.root)
        imageProcess.bitmap.observe(this) {
            feedImageBitmap = it
            Glide.with(this).load(it).into(dataBinding.ivProfileImage)
        }
        dataBinding.apply {
            btnBack.setOnClickListener { this@SubActivityCreatePost.finish() }
            mainUser.apply {
                tvNickname.text = username
                Glide.with(this@SubActivityCreatePost)
                        .load(profileimg)
                        .circleCrop()
                        .into(ivProfileImage)
            }
            tvFinish.setOnClickListener {
                AlertDialog.Builder(this@SubActivityCreatePost)
                        .setMessage("피드를 업로드하시겠습니까?")
                        .setPositiveButton("네") { dialog, which ->
                            dialog.dismiss()
                            feedViewModel.uploadFeed(feedData, feedImageBitmap!!,imageProcess)
                            feedViewModel.nowFeedUploadState.observe(this@SubActivityCreatePost) {
                                when (it) {
                                    FeedViewModel.State.Done -> {
                                        Toast.makeText(this@SubActivityCreatePost,
                                                "게시물이 업로드 되었습니다.", Toast.LENGTH_SHORT)
                                                .show()
                                        this@SubActivityCreatePost.finish()
                                    }
                                    FeedViewModel.State.Error ->
                                        Toast.makeText(this@SubActivityCreatePost,
                                                "게시물 업로드에 실패했습니다. \n 다시 시도해 주세요", Toast.LENGTH_SHORT)
                                                .show()
                                    else -> {}
                                }
                            }
                        }.setNegativeButton("아니요") { dialog, which ->
                            dialog.dismiss()
                        }.show()
            }
            //이미지 찾기
            btnImageUpload.setOnClickListener {
                imageProcess.initProcess()
            }
            tvFeedBookTitle.apply {
                isSingleLine = true //책 제목 한 줄로 표시하기
                ellipsize = TextUtils.TruncateAt.MARQUEE // 책 제목 흐르게 만들기
                isSelected = true // 선택하기
                setOnClickListener {
                    //책가져오는 메소드 작성
                    var intent = Intent(this@SubActivityCreatePost, search_fragment_subActivity_main::class.java)
                    intent.putExtra("classindex", 2)
                    bookResult.launch(intent)
                }

            }
            addLabel.setOnClickListener {
                //라벨 선택 후 화면에 표시 및 피드 데이터에 입력

            }
        }
    }
}