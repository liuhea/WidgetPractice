package com.liuhe.widget.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.liuhe.widget.bean.PieData
import com.liuhe.widget.utils.MathUtils
import java.lang.Math.toRadians
import java.util.*

/**
 *绘制饼状图
 *
 * 1. 最内侧扇形组成的圆形区域
 *      （1）定义一个起始角度
 *      （2）计算每一块扇形的弧度
 *      （3）遍历数据，每一个起始角度是上一个扇形的结束角度
 * 2. 中间短线段的绘制
 * 3. 最外侧文本的绘制
 *
 * @user liuhe
 * @date 27/03/2018
 */
class PieView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    /**
     * 画笔
     */
    lateinit var paint: Paint
    lateinit var linePaint: Paint

    /**
     * 路径
     */
    lateinit var path: Path
    /**
     * 数据源
     */
    var pies: List<PieData>? = null

    /**
     * 扇形的外接矩形
     */
    private lateinit var rectF: RectF
    private lateinit var touchRectF: RectF

    /**
     * 圆心
     */
    private var circleX = 0
    private var circleY = 0
    /**
     * 圆的半径
     */
    private var radius = 0f
    /**
     * 数据源总占比
     */
    private var totalRadio = 0f
    private lateinit var startAngles: Array<Float?>

    /**
     * 被点击的扇形区域
     */
    private var clickPosition: Int = 0

    init {
        initView()
    }

    private fun initView() {
        rectF = RectF()
        touchRectF = RectF()
        path = Path()

        paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
            // 抗锯齿（毛刺），开启后，占用内存
            isAntiAlias = true
            textSize = 20f
        }

        linePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            // 抗锯齿（毛刺），开启后，占用内存
            isAntiAlias = true
        }
    }

    /**
     * 当自定义控件的尺寸已经决定好之后的回调
     * 这个方法在onMeasure方法之后执行，最终的测量结果已经产生
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.circleX = w
        this.circleY = h
        /*
        * 防止绘制后，超出屏幕，获取屏幕宽高德较小值
         */
        var min = Math.min(w, h)
        radius = min * 0.7f / 2

        rectF.left = -radius
        rectF.top = -radius
        rectF.right = radius
        rectF.bottom = radius

        touchRectF.left = -radius - 15
        touchRectF.top = -radius - 15
        touchRectF.right = radius + 15
        touchRectF.bottom = radius + 5
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        // 移动画布，以中心为左边原点
        canvas?.translate(circleX.toFloat() / 2, circleY.toFloat() / 2)
        // 绘制扇形区域
        canvasPie(canvas)
        canvas?.restore()
    }

    /**
     * 绘制扇形区域
     */
    private fun canvasPie(canvas: Canvas?) {
        var startAngle = 0f
        pies?.forEach {
            var sweepAngle = it.ratio / totalRadio * 360
            paint.color = it.color.toInt()
            // 每次绘制要移动到原点
            path.moveTo(0f, 0f)
            // 绘制扇形，划过的角度-1，起始角度+1，保证每个扇形之间留有一定空隙

            if (pies!!.indexOf(it) == clickPosition) {
                path.arcTo(touchRectF, startAngle, sweepAngle - 0.3f)
//                canvas?.drawArc(touchRectF, startAngle, sweepAngle - 0.3f, true, paint)

            } else {
                path.arcTo(rectF, startAngle, sweepAngle - 0.3f)
//                canvas?.drawArc(rectF, startAngle, sweepAngle - 0.3f, true, paint)
            }
            canvas?.drawPath(path, paint)

            drawLines(startAngle, sweepAngle, canvas)

            drawText(startAngle, sweepAngle, canvas, it)

            // 点击区域判定用
            startAngles[pies!!.indexOf(it)] = startAngle

            // 每个扇形区域的起始点是上一个扇形区域的终点
            startAngle += sweepAngle + 0.3f

            // 在每次绘制扇形之后要对path重置操作，这样就可以清楚上一次绘制path使用的画笔的相关记录
            path.reset()
        }
    }

    /**
     * 绘制文本内容
     * 1. 文本内容
     * 2. 文本位置
     */
    private fun drawText(startAngle: Float, sweepAngle: Float, canvas: Canvas?, it: PieData) {
        val cos = Math.cos(toRadians((startAngle + sweepAngle / 2).toDouble()))
        val sin = Math.sin(toRadians((startAngle + sweepAngle / 2).toDouble()))
        var textX = (radius + 53) * cos.toFloat()
        var textY = (radius + 53) * sin.toFloat()
        val percent = String.format("%.1f", it.ratio / totalRadio * 100)

        if (startAngle % 360f >= 90f && startAngle % 360f < 270f) {
            //计算文本宽度
            val measureText = paint.measureText(percent)
            canvas?.drawText("$percent%", textX - measureText, textY, paint)
        } else {
            canvas?.drawText("$percent%", textX, textY, paint)
        }
    }

    /**
     * 1.绘制的直线的反向延长线经过圆心
     * 2.直线与圆的交底在对应扇形的中心点
     * 3.所有直线颜色一致
     *
     * 绘制直线的要素
     *  1.起点 Math.cos的参数是弧度制
     *      sinß=对边/斜边
     *  2.终点
     */
    private fun drawLines(startAngle: Float, currentRadio: Float, canvas: Canvas?) {
        val cos = Math.cos(toRadians((startAngle + currentRadio / 2).toDouble()))
        val sin = Math.sin(toRadians((startAngle + currentRadio / 2).toDouble()))
        var lineStartX = (radius * cos).toFloat()
        var lineStartY = (radius * sin).toFloat()

        var lineEndX = (radius + 50) * cos.toFloat()
        var lineEndY = (radius + 50) * sin.toFloat()
        canvas?.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, linePaint)
    }


    /**
     * 当用户与手机屏幕触摸时候，触摸事件
     * 1. 按下去
     * 2. 移动
     * 3. 抬起
     *
     * 参数：触摸事件，三种情况
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val action = event?.action

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                // [1]获取点击的位置距"当前视图"的左边缘距离
                var currentX = event.x
                var currentY = event.y
                // [2]将点击的x和y转换为以饼状图为圆心的坐标
                currentX -= circleX / 2
                currentY -= circleY / 2

                // [3]判断触摸点距离饼状图圆心的距离<饼状图对应圆的圆心
                val touchAngle = MathUtils.getTouchAngle(currentX, currentY)
                val touchRadius = Math.sqrt((currentX * currentX + currentY * currentY).toDouble())

                if (touchRadius < radius) {
                    try {
                        // 说明是一个有限区域，查找触摸角度是否位于起始角度集合中
                        // 参数2在参数1集合中的索引
                        // 未找到，返回-（在搜索的值附近的大于搜索值得正确值的索引值+1）
                        // {1,2,3}
                        // 搜索1，返回0
                        // 所有1.2，返回-（1+1）
                        val binarySearch = Arrays.binarySearch(startAngles, touchAngle)
                        // 未找到，则返回
                        clickPosition = if (binarySearch < 0) {
                            -binarySearch - 1 - 1 // 以10 20度为例，搜索值为15，我们需要在10的范围内的索引。所以，我们需要-1，为了让索引从0开始，再减1
                        } else {
                            binarySearch
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    // 让onDraw方法重新调用
                    invalidate()
                }
                println("pie->action_down-x=$currentX,y=$currentY")

            }

            MotionEvent.ACTION_MOVE -> {
                println("pie->action_move")
            }
            MotionEvent.ACTION_UP -> {
                println("pie->action_up")
            }
        }
        return super.onTouchEvent(event)
    }

    fun setData(data: List<PieData>) {
        this.pies = data

        pies?.forEach {
            totalRadio += it.ratio
        }
        startAngles = arrayOfNulls(pies?.size ?: 0)
    }
}