package com.example.bookworm.bottomMenu.feed

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.request.target.Target
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.bookworm.bottomMenu.search.bookitems.Book
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.databinding.CustomDialogLabelBinding
import com.example.bookworm.databinding.SubactivityModifyPostBinding
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.signature.ObjectKey


//게시물수정 액티비티
class SubActivityModifyPost : AppCompatActivity() {
    companion object {
        const val MODIFY_OK = 100 //수정 완료를 알려주는 Flag
    }

    private val feedViewModel by lazy {
        ViewModelProvider(this, FeedViewModel.Factory(this))[FeedViewModel::class.java]
    }
    private var binding: SubactivityModifyPostBinding? = null

    //원본 게시물 데이터
    private val originData by lazy {
        intent!!.getParcelableExtra<Feed>("Feed")!!
    }
    private var originBitmap: Bitmap? = null //원본 게시물 이미지 비트맵
    private var imgBitmap: Bitmap? = null //수정된 게시물 이미지 비트맵

    //    //액티비티 간 데이터 전달 핸들러(검색한 데이터의 값을 전달받는 매개체가 된다.) [책 데이터 이동]
//    private var bookResult = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//    ) { result: ActivityResult ->
//        if (result.resultCode == RESULT_OK) {
//            val intent = result.data
//            newData.book = intent!!.getParcelableExtra("bookData")!!
//            binding!!.tvFeedBookTitle.text = newData.book!!.title
//        }
//    }
    private lateinit var newData: Feed
    private lateinit var imageProcessing: ImageProcessing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SubactivityModifyPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        newData = originData.copy()
        imageProcessing = ImageProcessing(this)
        setUI()
    }

    private fun setUI() {
        var lastUri: Uri? = null
        binding!!.apply {
            ivpicture.apply {
                if (originData.imgurl != "") {
                    isVisible = true
                    Glide.with(this@SubActivityModifyPost).load(originData.imgurl)
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                        .listener(object : RequestListener<Drawable> {
                            //이미지 로딩 실퍂 시
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            //이미지 로딩 성공시
                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                originBitmap = resource!!.toBitmap()
                                imgBitmap = originBitmap
                                btnFeedImgDelete.isVisible = true
                                return false
                            }
                        }).into(this)
                }
            }
            btnBack.setOnClickListener {
                createAlert("btnBack")
            }
            btnImageUpload.setOnClickListener {
                imageProcessing.initProcess()
                imageProcessing.bitmapUri.observe(this@SubActivityModifyPost) { uri ->
                    if (uri != null && uri != lastUri) {
                        lastUri = uri //중복으로 받아와지는 문제를 해결하기 위함이다.
                        Glide.with(root).load(uri).listener(object : RequestListener<Drawable> {
                            //이미지 로딩 실퍂 시
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            //이미지 로딩 성공시
                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                imgBitmap = resource!!.toBitmap()
                                ivpicture.isVisible = true
                                btnFeedImgDelete.isVisible = true
                                return false
                            }
                        }).into(ivpicture)
                    }
                }
            }
            //게시물 이미지 삭제 버튼
            btnFeedImgDelete.setOnClickListener {
                ivpicture.isVisible = false
                imgBitmap = null
                btnFeedImgDelete.isVisible = false
            }
            tvFeedBookTitle.apply {
                text = newData.book.title
                // 수정 시엔 책 변경 불가
                setOnClickListener {
                    Toast.makeText(
                        this@SubActivityModifyPost,
                        "리뷰 수정 시엔 도서를 변경할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
//                setOnClickListener {
//                    val intent = Intent(this@SubActivityModifyPost, SearchMainActivity::class.java)
//                    if (newData.book != Book()) intent.putExtra("prevBook", newData.book)
//                    bookResult.launch(intent)
//                }
            }
            btnFinish.setOnClickListener {

                createAlert("upload")
            }
            edtFeedText.setText(newData.feedText)

            originData.creatorInfo.apply {
                Glide.with(this@SubActivityModifyPost).load(profileimg).circleCrop()
                    .into(ivProfileImage)
                tvNickname.text = username
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    //게시물 수정 로직
    private fun processUpdatePost() {
        binding!!.apply {
            newData.feedText = edtFeedText.text.toString()
            if (newData.feedText == "" || newData.book == Book()) {
                Toast.makeText(this@SubActivityModifyPost, "작성되지 않은 항목이 있습니다. ", Toast.LENGTH_SHORT)
                    .show()
            } else if (newData.feedText != originData.feedText || newData.book.itemId != originData.book.itemId || originBitmap != imgBitmap) {
                newData.date = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                //이곳에서 이미지를 서버에 올리고, 그 url을 받아 게시물을 작성해보아야겠다.
                val liveData = MutableLiveData<String>()
                if (imgBitmap == null) {
                    newData.imgurl = ""
                    sendDataToBeforeActivity()
                } else {
                    feedViewModel.getFeedImgUrl(newData, imgBitmap, imageProcessing, liveData)
                    liveData.observe(this@SubActivityModifyPost) {
                        //수정된 게시물 정보를 intent를 통해 이 액티비티를 호출한 부모 액티비티에 데이터를 전달한다.
                        if (it != null && !(imgBitmap != null && it == "")) {
                            newData.imgurl = it
                            sendDataToBeforeActivity()
                        }
                    }
                }

            } else finish() //기존내용에서 수정된 게 없는 경우 그냥 닫음
        }
    }

    private fun sendDataToBeforeActivity() {
        intent.putExtra("modifiedFeed", newData)
        setResult(MODIFY_OK, intent)
        finish()
    }

    private fun createAlert(type: String) {
        Dialog(this).apply {
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
                    "upload" -> {
                        tvMessage.text = "리뷰를 수정하시겠습니까?"
                        btnYes.setOnClickListener {
                            dismiss()
                            processUpdatePost()
                        }
                    }
                    "btnBack" -> {
                        tvMessage.text = "리뷰 수정을 그만하시겠습니까?"
                        btnYes.setOnClickListener {
                            dismiss()
                            finish()
                        }
                    }
                }
                show()
            }
        }
    }
}