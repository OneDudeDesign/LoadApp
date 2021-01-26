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

    private val progressAnimator = ValueAnimator()
    private val progressCircAnimator = ValueAnimator()
    private var progress: Float = 0f
    private var circProgress: Float = 0f

    //attribute variables
    private var colorTextPaint: Int = 0
    private var colorBtnStart: Int = 0
    private var colorBtnProgress: Int = 0
    private var colorLoadTextPaint: Int = 0

    //circle and arc variables
    var paintedTextsize: Float = 0f
    var xC: Float = 0f
    var yC: Float = 0f
    var rC: Float = 0f
    var arcL: Float = 0f
    var arcR: Float = 0f
    var arcT: Float = 0f
    var arcB: Float = 0f

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new -> }

    init {
        //get the attributes
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                colorTextPaint = getColor(R.styleable.LoadingButton_colorBtnText, Color.WHITE)
                colorBtnStart = getColor(R.styleable.LoadingButton_colorBtnStart, Color.BLUE)
                colorBtnProgress =
                    getColor(R.styleable.LoadingButton_colorBtnProgress, Color.YELLOW)
                colorLoadTextPaint =
                    getColor(R.styleable.LoadingButton_colorBtnLoadText, Color.BLACK)
            } finally {
                recycle()
            }
        }

    }

    override fun performClick(): Boolean {

        calculateProgressCircleValues()
        startAnimation()
        startCircAnimation()
        Timber.i("Button State: %s", buttonState.toString())
        return super.performClick()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        when (buttonState) {
            ButtonState.Completed -> drawButton(canvas)
            ButtonState.Clicked -> drawLoadingButton(canvas)
        }

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

        canvas?.drawRect(
            0.0f,
            0.0f,
            width.toFloat(),
            height.toFloat(),
            customBtnStartPaint()
        )
        //get the paint for the text
        val paintText = customButtonTextIdlePaint()

        //get offset value to center the text
        val textHeight: Float = paintText.descent() - paintText.ascent()
        val textOffset: Float = textHeight / 2 - paintText.descent()
        //Draw the text
        canvas?.drawText(
            context.getString(R.string.button_name),
            width / 2.0f, height / 2.0f + textOffset,
            paintText
        )

    }

    private fun drawLoadingButton(canvas: Canvas?) {
        //draw the background
        canvas?.drawRect(
            0.0f,
            0.0f,
            width.toFloat(),
            height.toFloat(),
            customBtnStartPaint()
        )
        //draw the animation
        canvas?.drawRect(
            0.0f,
            0.0f,
            progress,
            height.toFloat(),
            customBtnProgressPaint()
        )

        //draw the text
        //get the paint for the text
        val paintText = customButtonTextLoadPaint()

        //get offset value to center the text
        val textHeight: Float = paintText.descent() - paintText.ascent()
        val textOffset: Float = textHeight / 2 - paintText.descent()
        //Draw the text
        canvas?.drawText(
            context.getString(R.string.button_loading),
            width / 2.0f, height / 2.0f + textOffset,
            paintText
        )
        //measure the text here to place the text in a visually appealing location midway between
        // the end of the text and the end of the custom button
//        val paintedTextsize = paintText.measureText(R.string.button_loading.toString())
//        Timber.i(
//            "STRING IS OF LENGTH %s  OVERALL WIDTH IS %s HEIGHT IS %S",
//            paintedTextsize,
//            width,
//            height
       // )

        //set the points of the circle to draw via drawArc
//        val x = (width.toFloat() - ((width.toFloat() - paintedTextsize) / 4))
//        val y = height.toFloat() / 2.0f
//        val r = (height.toFloat() / 3.0f)

        //values for the arc:


        //Timber.i("X = %s, Y = %s, R = %s", x,y,r)
        canvas?.drawCircle(xC, yC, rC, customBtnProgressPaint())
        canvas?.drawArc(arcL,arcT, arcR,arcB, 270f, circProgress, true,customBtnStartPaint())

    }

    private fun calculateProgressCircleValues() {
        paintedTextsize =
            customButtonTextLoadPaint().measureText(R.string.button_loading.toString())
        xC = (width.toFloat() - ((width.toFloat() - paintedTextsize) / 4))
        yC = height.toFloat() / 2.0f
        rC = height.toFloat() / 3.0f
        Timber.i("X = %s, Y = %s, R = %s", xC, yC, rC)

        arcL = xC - rC
        arcR = xC + rC
        arcT = yC - rC
        arcB = yC + rC

        Timber.i("ArcLeft = %s, ArcRight = %s ArcTop = %s ArcBottom = %s", arcL,arcR,arcT,arcB)
    }

    private fun startAnimation() {

        progressAnimator.duration = 2500
        progressAnimator.repeatCount = ValueAnimator.INFINITE
        progressAnimator.setFloatValues(0.0f, width.toFloat())
        progressAnimator.addUpdateListener {
            progress = it.animatedValue as Float

            invalidate()
        }
        progressAnimator.start()

    }

    private fun startCircAnimation() {
        progressCircAnimator.duration = 2500
        progressCircAnimator.repeatCount = ValueAnimator.INFINITE
        progressCircAnimator.setFloatValues(0.0f, 360f)
        progressCircAnimator.addUpdateListener {
            circProgress = it.animatedValue as Float
            invalidate()
        }
        progressCircAnimator.start()
    }

    fun stopProgressAnimation() {
        Timber.i("StopProgressAnimation Called")
        progressAnimator.cancel()
        progressCircAnimator.cancel()
        invalidate()
    }

    private fun customBtnStartPaint(): Paint {
        val paintButtonStart = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = colorBtnStart
        }
        return paintButtonStart
    }

    private fun customBtnProgressPaint(): Paint {
        val paintButtonProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = colorBtnProgress
        }
        return paintButtonProgress
    }

    private fun customButtonTextIdlePaint(): Paint {
        //paint for the text
        val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = colorTextPaint
            textAlign = Paint.Align.CENTER
            textSize = 60.0f
            typeface = Typeface.create("", Typeface.BOLD)
        }
        return paintText
    }

    private fun customButtonTextLoadPaint(): Paint {
        //paint for the text
        val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = colorLoadTextPaint
            textAlign = Paint.Align.CENTER
            textSize = 60.0f
            typeface = Typeface.create("", Typeface.BOLD)
        }
        return paintText
    }


}