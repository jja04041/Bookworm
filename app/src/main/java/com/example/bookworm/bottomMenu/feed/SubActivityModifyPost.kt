package com.example.bookworm.bottomMenu.feed

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.databinding.SubactivityModifyPostBinding


//게시물수정 액티비티
class SubActivityModifyFeed : AppCompatActivity() {
    private var binding: SubactivityModifyPostBinding? = null
    private val originData by lazy {
        intent!!.getParcelableExtra<Feed>("Feed")
    }
    private val feedViewModel by lazy {
        ViewModelProvider(this, FeedViewModel.Factory(this)).get(
                FeedViewModel::class.java
        )
    }
    private lateinit var imageProcessing: ImageProcessing
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        binding = SubactivityModifyPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        imageProcessing = ImageProcessing(this)
        setUI()
    }

    private fun setUI() {
        binding!!.apply {
            ivpicture.apply {
                if (originData!!.imgurl != "") {
                    Glide.with(this@SubActivityModifyFeed).load(originData!!.imgurl).into(this)
                }
            }

            btnImageUpload.setOnClickListener {
                imageProcessing.initProcess()
            }
            btnFinish.setOnClickListener {
                feedViewModel.uploadFeed(originData!!, ivpicture.background.toBitmap(), imageProcessing)
            }

            originData!!.creatorInfo.apply {

            }
            imageProcessing.bitmap.observe(this@SubActivityModifyFeed) { bitmap ->
                Glide.with(this@SubActivityModifyFeed).load(bitmap).into(this.ivpicture)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}