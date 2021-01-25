package com.onedudedesign

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import timber.log.Timber
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    //attribute variables
    private var colorTextPaint:Int = 0
    private var colorBtnStart: Int = 0

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->}


    init {
        //get the attributes
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,0
        ).apply {
            try {
                colorTextPaint = getColor(R.styleable.LoadingButton_colorBtnText, Color.WHITE)
                colorBtnStart = getColor(R.styleable.LoadingButton_colorBtnStart,Color.BLUE)
            } finally {
                recycle()
            }
        }

    }

    override fun performClick(): Boolean {
        buttonState = ButtonState.Clicked
        Timber.i("Button State: %s", buttonState.toString())
        return super.performClick()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawButton(canvas)

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

    private fun drawButton(canvas: Canvas?) {
        val paintButtonStart = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = colorBtnStart
        }
        canvas?.drawRect(
            0.0f,
            0.0f,
            width.toFloat(),
            height.toFloat(),
            paintButtonStart
        )
        //paint for the text
        val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = colorTextPaint
            textAlign = Paint.Align.CENTER
            textSize = 40.0f
            typeface = Typeface.create("", Typeface.BOLD)
        }
        canvas?.drawText(
            context.getString(R.string.button_name),
            width / 2.0f, height / 2.0f,
            paintText
        )
    }

}