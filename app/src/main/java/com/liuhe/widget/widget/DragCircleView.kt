package com.liuhe.widget.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import com.liuhe.widget.utils.GeometryUtil
import com.liuhe.widget.utils.getStatusBarHeight


/**
 * 拖拽圆
 * @author liuhe
 * @date 2018-04-08
 */
class DragCircleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var paint: Paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        flags = Paint.ANTI_ALIAS_FLAG
    }
    private var textPaint: Paint = Paint().apply {
        color = Color.WHITE
        textSize = 35f
        style = Paint.Style.FILL
        flags = Paint.ANTI_ALIAS_FLAG
    }
    /**
     * 固定圆圆心
     */
    private var stableCircleCenter = PointF(200f, 200f)
    /**
     * 固定圆半径
     */
    private var stableCircleRadius = 30f

    /**
     * 拖拽圆圆心
     */
    private var dragCircleCenter = PointF(100f, 100f)
    /**
     * 拖拽圆半径
     */
    private var dragCircleRadius = 40f

    /**
     * 贝塞尔曲线参考点
     */
    private var controlPoint = PointF(150f, 150f)


    /**
     * 拖拽圆移动的最大距离
     */
    private var maxDragDistance = 500f
    /**
     * 固定圆变化的最小半径
     */
    private var minStableRadius = 5f

    /**
     * 拖拽是否超过范围，如果超过，就不绘制拖拽圆和中间图形
     */
    private var isOutRange: Boolean = false
    /**
     * 是否全部消失
     */
    private var isDisappear: Boolean = false
    /**
     * 绘制文本
     */
    private var text: Int = 0

    private var listener: OnDragViewChangeListener? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 调整画布，移除状态栏
        canvas?.save()
        // canvas 画布的坐标系是可见view左上角，而event.getRawX是相对屏幕的，所以为了防止误差需要减去
        canvas?.translate(0f, -getStatusBarHeight(this).toFloat())
        //随着拖拽圆和固定圆的圆心距离越来越大，固定圆的半径越来越小
        //拖拽圆和固定圆圆心的距离百分比变化== 固定圆半径的百分比变化
        val distance = GeometryUtil.getDistanceBetween2Points(dragCircleCenter, stableCircleCenter)
        val percent = distance / maxDragDistance

        var tempRadius = GeometryUtil.evaluateValue(percent, stableCircleRadius, dragCircleRadius)
        if (tempRadius < minStableRadius) {
            tempRadius = minStableRadius
        }

        if (!isDisappear) {
            if (!isOutRange) {
                // 计算两个圆圆心的斜率
                val dx = dragCircleCenter.x - stableCircleCenter.x
                val dy = dragCircleCenter.y - stableCircleCenter.y

                // 计算斜率
                var lineK = 0.0
                if (dx != 0f) {
                    lineK = ((dy / dx).toDouble())
                }
                //拖拽圆和固定圆附着点
                var dragPoints = GeometryUtil.getIntersectionPoints(dragCircleCenter, dragCircleRadius, lineK).toList()
                var stablePoints = GeometryUtil.getIntersectionPoints(stableCircleCenter, tempRadius, lineK).toList()
                controlPoint = GeometryUtil.getMiddlePoint(dragCircleCenter, stableCircleCenter)

                //绘制中间图形
                val path = Path().apply {
                    //1.移动到固定圆的附着点1
                    moveTo(stablePoints[0].x, stablePoints[0].y)
                    //2.向拖拽圆附着点1绘制贝塞尔曲线
                    quadTo(controlPoint.x, controlPoint.y, dragPoints[0].x, dragPoints[0].y)
                    //3.向拖拽圆附着点2绘制直线
                    lineTo(dragPoints[1].x, dragPoints[1].y)
                    //4.向固定圆附着点2绘制贝塞尔曲线
                    quadTo(controlPoint.x, controlPoint.y, stablePoints[1].x, stablePoints[1].y)
                    //5.闭合
                    close()

                }
                canvas?.drawPath(path, paint)
                // 6.重置路径，防止path重叠
                path.reset()
                canvas?.drawCircle(stableCircleCenter.x, stableCircleCenter.y, tempRadius, paint)
            }
            canvas?.drawCircle(dragCircleCenter.x, dragCircleCenter.y, dragCircleRadius, paint)
            drawText(canvas)
        }
        canvas?.restore()
    }

    private fun drawText(canvas: Canvas?) {
        if (text <= 0) {
            return
        }
//        val measureText = textPaint.measureText(text.toString())
        // 画图可以看出
        // x: 拖拽圆圆心横坐标-文本宽度/2
        // y: 拖拽圆圆心纵坐标+文本高度/2
        // 文本可以理解为在画布上一个矩形内的。
        val rect = Rect()
        textPaint.getTextBounds(text.toString(), 0, text.toString().length, rect)
        var x = dragCircleCenter.x - (rect.right - rect.left) / 2
        var y = dragCircleCenter.y + (rect.bottom - rect.top) / 2
        canvas?.drawText(text.toString(), x, y, textPaint)
    }

    /**
     * 设置text
     */
    fun setText(num: Int) {
        text = num
    }

    /**
     * 设置固定圆和拖拽圆坐标
     */
    fun setDragViewPosition(x: Float, y: Float) {
        dragCircleCenter.set(x, y)
        stableCircleCenter.set(x, y)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                isDisappear = false
                isOutRange = false
                // event.getX() 点击的点距离当前自定义控件左边缘的距离
                // event.getRawX() 点击的点距离屏幕左边缘的距离
                var downX = event.rawX
                var downY = event.rawY
                dragCircleCenter.set(downX, downY)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                var downX = event.rawX
                var downY = event.rawY
                dragCircleCenter.set(downX, downY)
                // 当拖拽圆超出一定范围后，固定圆和中间图形都消失了
                // 获取拖拽距离，判断距离是否超出
                val distance = GeometryUtil.getDistanceBetween2Points(dragCircleCenter, stableCircleCenter)
                if (distance > maxDragDistance) {
//                    拖拽是否超过范围，如果超过，就不绘制拖拽圆和中间图形
                    isOutRange = true
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                val distance = GeometryUtil.getDistanceBetween2Points(dragCircleCenter, stableCircleCenter)
                // 判断move的时候是否在最大范围之外
                if (isOutRange) {
                    // 判断up的时候是否在最大范围之外
                    if (distance > maxDragDistance) {
                        isDisappear = true
                        listener?.onDisappear()
                    } else {
                        dragCircleCenter.set(stableCircleCenter.x, stableCircleCenter.y)
                        //做重置操作
                        listener?.onReset()
                    }
                } else {
                    // move时候未超出最大范围，up时候也在最大范围内；
                    // 处理：让拖拽圆向固定圆圆心方向做平移运动，当到达固定圆圆心位置后，超出一段距离，在弹回。
                    // ValueAnimator: 当写入参数后，ValueAnimator会使其中一个参数向另一个参数变化
                    // 将up点瞬间的拖拽圆坐标记录
                    val tempDragCenter = PointF(dragCircleCenter.x, dragCircleCenter.y)
                    val va = ValueAnimator.ofFloat(distance, 0f)
                    // 添加数据变化的监听
                    va.addUpdateListener {
                        // 获取数据变化的百分比
                        val animatedFraction = it.animatedFraction
                        dragCircleCenter = GeometryUtil.getPointByPercent(tempDragCenter, stableCircleCenter, animatedFraction)
                        invalidate()
                    }
                    va.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            //做重置操作
                            listener?.onReset()
                        }
                    })
                    // 动画插值器：改变动画的执行效果；动画执行到目标点之后超出一段距离，然后回到原位
                    va.interpolator = OvershootInterpolator()
                    va.duration = 100
                    va.start()
                }
                invalidate()
            }
        }
        return true
    }

    fun setOnDragViewChangeListener(listener: OnDragViewChangeListener) {
        this.listener = listener
    }

    interface OnDragViewChangeListener {
        /**
         * GooView的消失处理
         */
        fun onDisappear()

        /**
         * GooView重置处理
         */
        fun onReset()
    }
}