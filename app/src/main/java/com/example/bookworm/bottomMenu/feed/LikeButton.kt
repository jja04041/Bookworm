package com.example.bookworm.bottomMenu.feed

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.withScale
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.bookworm.R
import com.google.android.material.math.MathUtils.lerp
import com.example.bookworm.extension.dp
import com.example.bookworm.extension.getBitmap
class LikeButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    sealed class UIState {
        object UnLike : UIState()
        class Like(val number: Long) : UIState()
        object Animating : UIState()
    }

    //################ Fraction##############
    private var fraction: Float = 0f

    //#######################################
    private val iconBitmap = getBitmap(R.drawable.ic_baseline_favorite)

    private val radius = 32 * dp()

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
    }

    private val srcInMode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val drawableCanvas = Canvas(this.iconBitmap)
    private val argbEvaluator = ArgbEvaluator()
    private var fromBgColor: Int = Color.parseColor("#f6f5f8")
    private var toBgColor: Int = Color.parseColor("#feecf0")
    private var fromBmColor: Int = Color.parseColor("#373c68")
    private var toBmColor: Int = Color.parseColor("#f6507d")
    private fun getRoundRectPaint() = paint.apply {
        val bgFraction = (fraction * 2).coerceAtMost(1f)
        color = argbEvaluator.evaluate(bgFraction, fromBgColor, toBgColor) as Int
    }

    private fun getRipplePaint() = paint.apply {
        color = Color.GREEN
    }

    private fun getBitmapPaint() = paint.apply {
        val bgFraction = (fraction * 2).coerceAtMost(1f)
        color = argbEvaluator.evaluate(bgFraction, fromBmColor, toBmColor) as Int
    }

    private fun tintBitmap() {
        // change to src in
        paint.xfermode = srcInMode
        drawableCanvas.drawRect(0f, 0f, this.iconBitmap.width.toFloat(), this.iconBitmap.height.toFloat(), getBitmapPaint())
        paint.xfermode = null
    }

    private fun updateBgColor(uiState: UIState) {
        if (uiState == UIState.UnLike) {
            toBgColor = Color.parseColor("#f6f5f8")
            toBmColor = Color.parseColor("#373c68")
        } else {
            toBgColor = Color.parseColor("#feecf0")
            toBmColor = Color.parseColor("#f6507d")
        }
        if (this.uiState == UIState.UnLike) {
            fromBgColor = Color.parseColor("#f6f5f8")
            fromBmColor = Color.parseColor("#373c68")
        } else {
            fromBgColor = Color.parseColor("#feecf0")
            fromBmColor = Color.parseColor("#f6507d")
        }
    }

    override fun onDraw(canvas: Canvas) {
        // ripple effect
        getRipplePaint().also {
            val x = width / 2f
            val y = height / 2f
            val radius = x
//            canvas.drawCircle(x, y, radius, it)
        }

        // round rect
        getRoundRectPaint().also {
            // bound
            val left = 0f
            val top = 0f
            val right = width.toFloat()
            val bottom = height.toFloat()
            // scale
            val scaleX = lerp(1f, 0.9f, 1f, fraction)
            val scaleY = scaleX
            val pivotX = width / 2f
            val pivotY = height / 2f
            canvas.withScale(scaleX, scaleY, pivotX, pivotY) {
                canvas.drawRoundRect(left, top, right, bottom, radius, radius, it)
            }
        }

        // heart icon
        getBitmapPaint().also { paint ->
            val scaleX = lerp(1f, 0.6f, 1f, fraction)
            val scaleY = scaleX
            val pivotX = width / 2f
            val pivotY = height / 2f
            canvas.withScale(scaleX, scaleY, pivotX, pivotY) {
                val left = width / 2f - iconBitmap.width / 2f
                val top = height / 2f - iconBitmap.height / 2f
                tintBitmap()
                canvas.drawBitmap(iconBitmap, left, top, paint)
            }
        }
    }

    private var uiState: UIState = UIState.UnLike

    fun setUIState(uiState: UIState, isAnim: Boolean) {
        updateBgColor(uiState)
        if (this.uiState == UIState.Animating) {
            return
        }
        if (isAnim) {
            runAnimation().apply {
                doOnEnd {
                    this@LikeButton.uiState = uiState
                }
            }
        } else {
            this.uiState = uiState
            fraction = 0f
            invalidate()
        }
    }
    fun getUIState(): UIState {
        return this.uiState
    }

    private fun runAnimation(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                fraction = it.animatedValue as Float
                invalidate()
            }
            interpolator = FastOutSlowInInterpolator()
            doOnStart {
                this@LikeButton.uiState = UIState.Animating
            }
            duration = 500L
            start()
        }
    }

    private fun lerp(a: Float, b: Float, c: Float, fraction: Float): Float {
        return if (fraction <= 0.5f) {
            lerp(a, b, fraction * 2)
        } else {
            val tempFraction = fraction - 0.5f
            lerp(b, c, tempFraction * 2)
        }
    }
}