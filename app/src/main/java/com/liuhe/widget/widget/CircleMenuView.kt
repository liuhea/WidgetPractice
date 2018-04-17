package com.liuhe.widget.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.liuhe.widget.R
import com.liuhe.widget.utils.CircleUtils

/**
 * 自定义银行
 * @author liuhe
 * @date 2018-04-01
 */
class CircleMenuView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attributes, defStyleAttr) {
    val TAG = "CircleMenu"

    private var startAngle = 0f
    // 圆形直径
    private var finalD = 600

    /**
     * 处理控件及其子控件的测量
     * 在自定义ViewGroup中，系统默认只处理当前控件的测量，不对子视图进行测量，我们需要重写onMeasure实现对子视图进行测量。
     */
    @SuppressLint("SwitchIntDef")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // setMeasuredDimension 设置控件最终的测量宽高
        // 根据需求不同，有时候直接复写super.onMeasure()无法满足，比如在xml中定义此控件大小，可能导致控件背景变形，这时候就要重写onMeasure()方法。
        // eg:需求：不管用户传递多大尺寸，都要完全显示当前自定义控件。
        var measureHeight = 0
        var measureWidth = 0
        //判断模式：判断主要处理两种，1.确切，2.至多
        var mode = MeasureSpec.getMode(widthMeasureSpec)
        var size = MeasureSpec.getSize(widthMeasureSpec)
        val density = context.resources.displayMetrics.density
        println("CicleMenuView->mode=$mode -- size=$size -- desity=$density")
        when (mode) {
            MeasureSpec.EXACTLY -> {
                // 判断传入的宽度和屏幕宽度取最小值
                measureWidth = Math.min(size, getDefaultWidth())
                measureHeight = Math.min(size, getDefaultWidth())
            }
//            MeasureSpec.AT_MOST
//            MeasureSpec.UNSPECIFIED
            else -> {
                // wrap_content， 未指定；
                // 有背景，看具体背景多大背景有多大，控件就有多大；
                // 没有背景，判断将默认宽度作为控件宽度。suggestedMinimumWidth==0,无背景
                if (suggestedMinimumWidth == 0) {
                    measureWidth = getDefaultWidth()
                    measureHeight = getDefaultWidth()
                } else {
                    measureWidth = Math.min(suggestedMinimumWidth, getDefaultWidth())
                    measureHeight = Math.min(suggestedMinimumHeight, getDefaultWidth())
                }
            }
        }
        println("CircleMenuView->mode$mode->suggestedMinimumWidth=$suggestedMinimumWidth->measureWidth=$measureWidth->measureHeight=$measureHeight")
        finalD = measureWidth
        setMeasuredDimension(measureWidth, measureHeight)

        for (i in 0 until childCount) {
            val child = this.getChildAt(i)
            // 默认系统是可以通过onMeasure给予MeasureSpec，而通过inflate进来的子视图是没有MeasureSpc参数的
            // 故需要我们自己设计MeasureSpec
            val makeMeasureSpec = MeasureSpec.makeMeasureSpec(finalD / 3, MeasureSpec.EXACTLY)
            child.measure(makeMeasureSpec, makeMeasureSpec)
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childWidth = child.measuredWidth

            // temp: 圆心与子视图所在矩形中心的距离
            var temp = finalD / 3.0f
            var left = finalD / 2 + Math.round(temp * Math.cos(Math.toRadians(startAngle.toDouble()))) - childWidth / 2
            var top = finalD / 2 + Math.round(temp * Math.sin(Math.toRadians(startAngle.toDouble()))) - childWidth / 2
            var right = left + childWidth
            var bottom = top + childWidth

            child.layout(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            startAngle += 360 / childCount
        }
    }

    // ACTION_DOWN时，保存的点
    private var lastX = 0f
    private var lastY = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y
        val action = event?.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "ACTION_DOWN -（x，y)=($x,$y)")
                lastX = x!!
                lastY = y!!
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "ACTION_MOVE-（x，y)=($x,$y)")
                Log.d(TAG, "ACTION_MOVE-（lastX，lastX)=($lastX,$lastX)")
                var start = CircleUtils.getAngle(lastX, lastY, finalD)
                var end = CircleUtils.getAngle(x!!, y!!, finalD)

                // 判断点击的点所处的象限，如果是1.4象限，角度值为正数，否则为负数。？？
                // 顺时针  4象限 ：0~90， 3象限：90~0 2象限：0~-90 1象限：-90~0
                var angle = if (CircleUtils.getQuadrant(x, y, finalD) == 1 || CircleUtils.getQuadrant(x, y, finalD) == 4) {
                    end - start
                } else {
                    start - end
                }

                Log.d(TAG, "ACTION_MOVE-start-->end=($start-->$end)")

                startAngle += angle
                // 让界面重新布局和绘制
                requestLayout()

                //滑动停止，但是尚未松手的点。
                lastX = x
                lastY = y
            }

            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "ACTION_UP-（x，y)=($x,$y)")
            }
        }
        // true表示当前控件想要处理事件，所有的MotionEvent事件都交给自己处理
        return true
    }

    fun setData(texts: List<String>, imgIds: List<Int>) {
        for (i in 0 until texts.size) {
            val rootView = View.inflate(context, R.layout.item_circle_menu, null)
            val iv = rootView.findViewById<ImageView>(R.id.iv)
            val txt = rootView.findViewById<TextView>(R.id.tv)

            iv.setImageResource(imgIds[i])
            txt.text = texts[i]
            addView(rootView)
        }
    }

    private fun getDefaultWidth(): Int {
        // 获取屏幕宽高
        val displayMetrics = context.applicationContext.resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels
        val heightPixels = displayMetrics.heightPixels
        // 获取宽度和高度的较小值
        return Math.min(widthPixels, heightPixels)
    }
}