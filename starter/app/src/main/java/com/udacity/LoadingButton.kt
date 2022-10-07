package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var buttonText = resources.getString(R.string.button_name)
    private var buttonProgress = 0f
    private var circleProgress = 0f

    private val animator = ValueAnimator.ofFloat(0f, 1f)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when(new) {
            ButtonState.Clicked -> handleClick()
            ButtonState.Completed -> handleCompleted()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
    }

    private var textColor = 0
    private var buttonBackgroundColor = 0


    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
        }

        animator.repeatCount = 0
        animator.duration = 2000

        animator.addUpdateListener {
            buttonProgress = it.animatedValue as Float
            circleProgress = it.animatedValue as Float * 360f
            if(buttonProgress >= 1.0f) {
                buttonState = ButtonState.Completed
            }
            invalidate()
        }
    }

    private fun handleClick() {
        isClickable = false
        buttonText = resources.getString(R.string.button_loading)
        animator.start()
        invalidate()
        buttonState = ButtonState.Loading
    }

    private fun handleCompleted() {
        buttonText = resources.getString(R.string.button_name)
        animator.cancel()
        buttonProgress = 0f
        circleProgress = 0f
        invalidate()
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = buttonBackgroundColor
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = resources.getColor(R.color.colorPrimaryDark)
        canvas.drawRect(0f, 0f, widthSize.toFloat()*buttonProgress, heightSize.toFloat(), paint)

        paint.color = resources.getColor(R.color.colorAccent)
        canvas.drawArc(
            (widthSize.toFloat() / 4) * 3,
            heightSize.toFloat() / 2 - heightSize.toFloat() / 4,
            (widthSize.toFloat() / 4) * 3 + heightSize.toFloat() / 2,
            heightSize.toFloat() / 2 + heightSize.toFloat() / 4,
            0f,
            circleProgress,
            true,
            paint
        )

        paint.color = textColor
        canvas.drawText(buttonText, widthSize.toFloat() / 2, (heightSize.toFloat() - (paint.descent() + paint.ascent())) / 2, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Clicked
        return true
    }
}