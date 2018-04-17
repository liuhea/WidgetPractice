package com.liuhe.widget.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.util.TypedValue


/**
 * Created by liuhe on 27/03/2018.
 *
 * canvas 旋转-rotate、偏移-translate、缩放-scale
 */
class PathView(context: Context) : View(context) {
    private lateinit var greenPaint: Paint
    private lateinit var grayPaint: Paint

    // 刻度尺高度
    private val DIVIDING_RULE_HEIGHT = 70

    // 距离左右间
    private val DIVIDING_RULE_MARGIN_LEFT_RIGHT = 10

    private val TOTAL_SQUARE_COUNT = 9


    // 第一条线距离边框距离
    private val FIRST_LINE_MARGIN = 5
    // 打算绘制的厘米数
    private val DEFAULT_COUNT = 10
    private var mDividRuleHeight: Int = 0
    private var mHalfRuleHeight: Int = 0
    private var mFirstLineMargin: Int = 0
    private var mDividRuleLeftMargin: Int = 0


    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : this(context) {
        init()
    }

    private fun init() {
        greenPaint = Paint().apply {
            color = Color.GREEN
            //设置防抖动
            isDither = true
            strokeWidth = 5f
            //抗锯齿
            isAntiAlias = true
            style = Paint.Style.STROKE

        }
        grayPaint = Paint().apply {
            color = Color.GRAY
            //设置防抖动
            isDither = true
            // 画笔宽度
            strokeWidth = 2f
            //抗锯齿
            isAntiAlias = true
            style = Paint.Style.STROKE
            textSize = 40f

        }

        mDividRuleHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DIVIDING_RULE_HEIGHT.toFloat(), resources.displayMetrics).toInt()
        mHalfRuleHeight = mDividRuleHeight / 2

        mDividRuleLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DIVIDING_RULE_MARGIN_LEFT_RIGHT.toFloat(), resources.displayMetrics).toInt()
        mFirstLineMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                FIRST_LINE_MARGIN.toFloat(), resources.displayMetrics).toInt()

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

//        drawPath(canvas)
//        drawQuad(canvas)
//        doOtherThings(canvas)

        drawRuler(canvas)

        drawSquare(canvas)

        drawClock(canvas)
    }

    /**
     * 绘制闹钟表盘
     */
    private fun drawClock(canvas: Canvas?) {
        var halfWidth = (width / 2).toFloat()
        var halfHeight = (height / 2).toFloat()
        var radius = width / 2.toFloat() - 10
        var longWidth = 50
        var shortWidth = 20
        var lineTop = halfHeight - radius
        var lineBottom = 0f
        canvas?.drawCircle(halfWidth, halfHeight, radius, grayPaint)
        for (i in 0..360) {
            if (i % 30 == 0) {
                lineBottom = (lineTop + longWidth)
                greenPaint.strokeWidth = 4F
            } else {
                lineBottom = (lineTop + shortWidth)
                greenPaint.strokeWidth = 2F
            }

            if (i % 6 == 0) {
                canvas?.save()
                greenPaint.color = Color.RED
                canvas?.rotate(i.toFloat(), halfWidth, halfHeight)
                canvas?.drawLine(halfWidth, lineTop, halfWidth, lineBottom, greenPaint)
                canvas?.restore()
            }
        }
        greenPaint.color = Color.GREEN

    }

    /**
     * 绘制正方形
     */
    private fun drawSquare(canvas: Canvas?) {
        val rect = Rect(200, 400, 600, 800)
        for (i in 0 until TOTAL_SQUARE_COUNT) {
            // 保存画布
            canvas?.save()
            val fraction = i.toFloat() / TOTAL_SQUARE_COUNT
            // 将画布以正方形中心进行缩放
//            前两个参数为将画布在x、y方向上缩放的倍数，而px和py 分别为缩放的基准点，如下图所示：
            canvas?.scale(fraction, fraction, 400f, 600f)
            canvas?.drawRect(rect, greenPaint)
            // 画布回滚
            canvas?.restore()
        }
    }


    /**
     * 绘制一个尺子
     *
     * ref: https://blog.csdn.net/tianjian4592/article/details/45234419
     */
    private fun drawRuler(canvas: Canvas?) {
        //外框
        drawOuter(canvas)
        // 刻度
        drawLines(canvas)
        // 数字
        drawNumbers(canvas)
    }

    private fun drawNumbers(canvas: Canvas?) {
        var mLineInterval = (measuredWidth - 2 * mDividRuleLeftMargin - 2 * mFirstLineMargin) / (DEFAULT_COUNT)

        canvas?.save()
        for (i in 0..DEFAULT_COUNT * 10) {
            top = (mDividRuleHeight + 100) - 50
            canvas?.drawText(i.toString(), (mDividRuleLeftMargin).toFloat(), top.toFloat() - 10, grayPaint)
            canvas?.translate(mLineInterval.toFloat(), 0f)
        }
        canvas?.restore()
    }

    /**
     * 绘制刻度线
     */
    private fun drawLines(canvas: Canvas?) {
        var mLineInterval = (measuredWidth - 2 * mDividRuleLeftMargin - 2 * mFirstLineMargin) / (DEFAULT_COUNT * 10)
        canvas?.save()
        canvas?.translate((mDividRuleLeftMargin + mFirstLineMargin).toFloat(), 0f)
        var top = 50
        for (i in 0..DEFAULT_COUNT * 10) {
            top = when {
                i % 10 == 0 -> (mDividRuleHeight + 100) - 50
                i % 5 == 0 -> (mDividRuleHeight + 100) - 30
                else -> (mDividRuleHeight + 100) - 15
            }

            canvas?.drawLine(0f, (mDividRuleHeight + 100).toFloat(), 0f, top.toFloat(), greenPaint)
            canvas?.translate(mLineInterval.toFloat(), 0f)

        }
        canvas?.restore()
    }

    private fun drawOuter(canvas: Canvas?) {
        val outRectF = Rect(mDividRuleLeftMargin, 100, (measuredWidth - mDividRuleLeftMargin),
                mDividRuleHeight + 100)
        canvas?.drawRect(outRectF, grayPaint)
    }

    /**
     * 绘制贝塞尔曲线
     * 1.起始点
     * 2.终点
     * 3.控制点：control point
     */
    private fun drawQuad(canvas: Canvas?) {
        val path = Path().apply {
            // 将path移动到绘制的起始点
            moveTo(200f, 200f)
            // 贝塞尔曲线-控制点、终点
            quadTo(500f, 400f, 200f, 500f)
            close()
        }
        canvas?.drawPath(path, greenPaint)
    }

    /**
     * 绘制线
     */
    private fun drawPath(canvas: Canvas?) {

        val path = Path().apply {
            // 将path移动到绘制的起始点
            moveTo(200f, 200f)
            // 以moveTo之后的点为起始点，以参数1，2为终点绘制一条线段
            lineTo(500f, 200f)
            lineTo(500f, 500f)
            lineTo(200f, 500f)
            close()
        }

        canvas?.drawPath(path, greenPaint)

//        canvas?.restore()
    }

    /**
     * canvas其他操作
     * 平移、旋转、缩放等操作之前必须先调用save方法处理后再调用restore方法
     *
     * 平移和旋转实际是对坐标系的平移和旋转。
     *
     * sava(): 用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作。
     * restore(): 用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响。
     * save和restore要配对使用（restore可以比save少，但不能多），如果restore调用次数比save多，会引发Error。
     */
    private fun doOtherThings(canvas: Canvas?) {
        canvas?.save()
        // 平移距离，dx,dy
//        canvas?.translate(100f, 0f)
        // 旋转角度
        canvas?.rotate(10f)
        //缩放比例，sx,sy
//        canvas?.scale(1.2f,0.5f)
        drawPath(canvas)
        canvas?.restore()
    }
}