package com.example.bookworm

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


//데이터 바인딩을 위한 어댑터

//이미지 로딩 상태를 보여줘야 함.,,
object BindingAdapter {

    @JvmStatic
    @BindingAdapter("app:imgUrl", "app:placeholder", "app:type")
    fun setImage(v: ImageView, url: String, placeholder: Drawable, type: String?) {
        val circularProgressDrawable = CircularProgressDrawable(v.context)
        circularProgressDrawable.apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
            Glide.with(v.context)
                .load(url)
                .placeholder(this)
                .error(placeholder)
                .apply {
                    if (type == "profile") {
                        diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).circleCrop()
                            .into(v)
                    } else into(v)
                }
        }
    }
}