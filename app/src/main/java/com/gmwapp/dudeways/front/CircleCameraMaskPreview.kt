package com.gmwapp.dudeways.front

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat


class CircleCameraMaskPreview : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var mPaintMask: Paint = Paint()
    private var mPaintCircle: Paint = Paint()
    private var mPorterDuffMode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private var mStartColor: Int = android.R.color.holo_red_light
    private var mEndColor: Int = android.R.color.holo_blue_dark

    init {
        initView()
    }

    private fun initView() {
        mPaintMask.alpha = 0
        mPaintMask.isAntiAlias = true
        mPaintMask.color = Color.BLACK
        mPaintMask.style = Paint.Style.FILL

        mPaintCircle.color = ContextCompat.getColor(context, android.R.color.holo_blue_light)
        mPaintCircle.style = Paint.Style.STROKE
        mPaintCircle.strokeWidth = context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp)

        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    fun setBorderGradientColor(startColor: Int, endColor: Int) {
        mPaintMask.xfermode = null
        mPaintCircle.color = ContextCompat.getColor(context, endColor)
        mPaintCircle.shader = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            width.toFloat(),
            ContextCompat.getColor(context, startColor),
            ContextCompat.getColor(context, endColor),
            Shader.TileMode.REPEAT
        )
        mStartColor = startColor
        mEndColor = endColor
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPaintCircle.shader = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            width.toFloat(),
            ContextCompat.getColor(context, mStartColor),
            ContextCompat.getColor(context, mEndColor),
            Shader.TileMode.REPEAT
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (canvas != null) {
            super.onDraw(canvas)
        }
        canvas?.drawPaint(mPaintMask)
        canvas?.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2).toFloat() - context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp),
            mPaintCircle
        )
        mPaintMask.xfermode = mPorterDuffMode
        canvas?.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2).toFloat() - context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp),
            mPaintMask
        )


    }
}