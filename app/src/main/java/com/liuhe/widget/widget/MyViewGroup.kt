package com.liuhe.widget.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import com.liuhe.widget.R
import com.liuhe.widget.utils.log

/**
 *  自定义ViewGroup
 *
 *  源码可知：
 *
 *  down事件确定target,递归调用dispatchTouchEvent()
 *
 *  情况1.
 *      ViewGroup 和 子View onTouchEvent同时返回true,子View处理事件，父View没有机会处理。
 *      onTouchEvent返回true,仅仅表示想要处理事件，如果多个视图均返回true,则事件优先交给子视图。
 *   情况2：
 *      ViewGroup迫切需要处理事件，即onInterceptTouchEvent()返回true，而子View即使onTouchEvent返回true,也不管用。
 *      ViewGroup处理事件。
 *
 *  情况3：即使ViewGroup使用了onInterceptTouchEvent返回true,但是子view使用了requestDisallowInterceptTouchEvent(),子view成为target,处理事件
 *
 * @author liuhe
 * @date 2018-04-01
 */
class MyViewGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    init {

        for (i in 0..5) {
            addView(ImageView(getContext()).apply {
                setImageResource(R.mipmap.ic_launcher)
                setOnTouchListener { _, _ ->
                    "I'm from ImageView".log()
                    false
                }
            })
        }
    }

    //继承ViewGroup必须实现的方法，用于对子视图的摆放
    // 当前控件对应的边的左上右下
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // 获取对应位置的孩子视图
        val childAt = this.getChildAt(0)
//        // 摆放孩子视图，layout方法可以用来改变图形的大小
        childAt.layout(0, 0, 300, 300)

//        // 正三角
//        var childIndex = 0
//        var top = 0
//        for (i in 1..3) {
//            println("MyViewGroup-->i=$i")
//            for (j in 0 until i) {
//                println("MyViewGroup-->j=$j")
//                val child = this.getChildAt(childIndex)
//                child.layout(100 * j, top, 100 * (j + 1), top + 100)
//                childIndex++
//            }
//            top += 100
//        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        "i'm form MyViewGroup".log()
        return true
    }
}
