package com.example.bookworm.extension

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

fun View.sp() = resources.displayMetrics.scaledDensity
fun View.dp() = resources.displayMetrics.density
fun View.getDrawableX(drawableId: Int): Drawable {
    return ContextCompat.getDrawable(context, drawableId)!!
}

fun View.getColorX(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(context, colorRes)
}

fun View.getBitmap(drawableId: Int): Bitmap {
    return ContextCompat.getDrawable(context, drawableId)!!.toBitmap()
}