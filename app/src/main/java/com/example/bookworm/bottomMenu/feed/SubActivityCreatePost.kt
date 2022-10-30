package com.example.bookworm.bottomMenu.feed


import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bookworm.LoadState
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchMainActivity
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.CustomDialogLabelBinding
import com.example.bookworm.databinding.SubactivityCreatePostBinding
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


//기존 피드 작성 액티비티에서 난잡하게 꼬여 있던 코드들을 MVVM 패턴에 맞게 재구성
//라벨 추가 및 수정 부분을 추가해야 함 .


class SubActivityCreatePost : AppCompatActivity() {
    private val dataBinding by lazy {
        SubactivityCreatePostBinding.inflate(layoutInflater)
    }

    companion object {
        val CREATE_OK = 30
    }

    private val feedData by lazy {
        Feed().apply {
            val mainUserKeyword = "mainUser"
            intent.apply {
                isUserPost = true
                creatorInfo = if (hasExtra(mainUserKeyword)) getParcelableExtra(mainUserKeyword)!! else UserInfo()
                userToken = creatorInfo.token
                book = if (intent.hasExtra("BookData")) {
                    intent.getParcelableExtra("BookData")!!
                } else Book()
            }
            dataBinding.tvFeedBookTitle.text = book.title
        }
    }
    private var feedImageBitmap: Bitmap? = null
    private val imageProcess by lazy {
        ImageProcessing(this)
    }
    private val feedViewModel by lazy {
        ViewModelProvider(this, FeedViewModel.Factory(this))[FeedViewModel::class.java]
    }


    //액티비티 간 데이터 전달 핸들러(검색한 데이터의 값을 전달받는 매개체가 된다.) [책 데이터 이동]
    var bookResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            feedData.book = intent!!.getParcelableExtra("bookData")!!
            dataBinding.tvFeedBookTitle.text = feedData.book!!.title
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(dataBinding.root)

        //파이어베이스에 피드 url이 올라갈 수 있게 해주는 코드
        imageProcess.bitmap.observe(this) {
            feedImageBitmap = it
            Glide.with(this).load(it).into(dataBinding.ivpicture)
        }

        //피드 작성화면에서 사진이 보이게 해주는 코드
        imageProcess.bitmapUri.observe(this, Observer { it: Uri? ->
            Glide.with(this).load(it)
                    .into(dataBinding.ivpicture)
        })

        dataBinding.apply {
            btnBack.setOnClickListener {
                createAlert("back")
            }



            feedData.creatorInfo.apply {
                tvNickname.text = username!!
                Glide.with(this@SubActivityCreatePost)
                        .load(profileimg)
                        .circleCrop()
                        .into(ivProfileImage)
            }
            edtFeedText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    edtFeedText.apply {
                        if (this.text.toString() != "") feedData.feedText = this.text.toString()
                        else feedData.feedText = null
                    }
                }

            })
            tvFinish.setOnClickListener {
                createAlert("upload")
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
                    val intent = Intent(this@SubActivityCreatePost, SearchMainActivity::class.java)
                    if (feedData.book != Book()) intent.putExtra("prevBook", feedData.book)
                    bookResult.launch(intent)
                }

            }
        }
    }


    private fun createAlert(type: String) {
        Dialog(this@SubActivityCreatePost).apply {
            val dialogBinding = CustomDialogLabelBinding.inflate(layoutInflater)
            this.setContentView(dialogBinding.root)
            dialogBinding.apply {
                root.layoutParams = root.layoutParams.apply {
                    width = 1000
                    height = LinearLayout.LayoutParams.MATCH_PARENT
                }

                btnNo.setOnClickListener {
                    dismiss()
                }
                when (type) {
                    "back" -> {
                        tvMessage.text = "피드 작성을 그만 하시겠습니까?"
                        btnYes.setOnClickListener {
                            this@SubActivityCreatePost.finish()
                            dismiss()
                        }
                        show()
                    }
                    else -> {
                        tvMessage.text = "피드를 업로드하시겠습니까?"
                        btnYes.setOnClickListener {
                            if (feedData.book == Book() || feedData.feedText == "") {
                                Toast.makeText(context, "내용을 입력한 후 완료를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                            }else if(feedData.feedText!!.length<10){
                                Toast.makeText(context, "리뷰를 10자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                processUpload()
                            }
                            dismiss()
                        }
                        show()
                    }
                }
            }
        }
    }

    private fun processUpload() {
        feedData.date = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        feedData.feedID = System.currentTimeMillis().toString() + "_" + feedData.userToken //현재 시각 + 사용자 토큰을 FeedID로 설정
        feedViewModel.uploadPost(feedData, feedImageBitmap, imageProcess)
        feedViewModel.nowFeedUploadState.observe(this@SubActivityCreatePost) {
            when (it) {
                LoadState.Done -> {
                    intent.putExtra("feedData", feedData)
                    setResult(CREATE_OK, intent)
                    this@SubActivityCreatePost.finish()
                }
                LoadState.Error ->
                    Toast.makeText(this@SubActivityCreatePost,
                            "게시물 업로드에 실패했습니다. \n 다시 시도해 주세요", Toast.LENGTH_SHORT)
                            .show()
                else -> {}
            }
        }
    }

}