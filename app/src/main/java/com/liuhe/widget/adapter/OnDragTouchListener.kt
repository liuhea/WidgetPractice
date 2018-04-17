package com.liuhe.widget.adapter

import android.content.Context
import android.graphics.PixelFormat
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.liuhe.widget.widget.DragCircleView

/**
 *
 * @author liuhe
 * @date 2018-04-12
 */
class OnDragTouchListener(context: Context) : View.OnTouchListener, DragCircleView.OnDragViewChangeListener {

    /**
     * WindowManager:这个类可以在任何的界面情况下添加一个额外的视图
     */
    private var manager: WindowManager? = null
    private var circleView: DragCircleView? = null
    private var params: WindowManager.LayoutParams? = null
    private var txt: TextView? = null


    init {
        manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        circleView = DragCircleView(context).apply { setOnDragViewChangeListener(this@OnDragTouchListener) }
        params = WindowManager.LayoutParams()?.apply {
            height = WindowManager.LayoutParams.MATCH_PARENT
            width = WindowManager.LayoutParams.MATCH_PARENT
            //类型是透明
            format = PixelFormat.TRANSLUCENT
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        //获取TextView控件的父布局即条目的根部局,通过根部局,让RecyclerView不要拦截事件
        v?.parent?.requestDisallowInterceptTouchEvent(true)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                txt = (v as TextView).also { it.visibility = View.INVISIBLE }
                val rawX = event.rawX
                val rawY = event.rawY
                circleView?.setDragViewPosition(rawX, rawY)
                circleView?.setText(v.text.toString().toInt())
                manager?.addView(circleView, params)
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        // 请求rcy不拦截事件后,但是事件并没有给小球，所以可以将事件交给拖拽圆
        circleView?.onTouchEvent(event)
        return true
    }

    override fun onDisappear() {
        manager?.removeView(circleView)
    }

    override fun onReset() {
        //重置操作
        //1,移除GooView
        //WindowManager的addView方法是将circleView添加到root根部局中
        //removeView是将circleView从root根部局中移除,当移除后的GooView再次尝试从root中移除就会抛出
        //View not attached to window manager这样的异常
        //是由已经被WindowManager移除的视图,再此被移除
        //如果已经添加到root上的GooView会有一个Parent(父视图),判断父视图是否为空,就可以规避这个bug
        if (circleView?.parent != null) {
            manager?.removeView(circleView)
            //2,让TextView显示出来
            txt?.visibility = View.VISIBLE
        }
    }
}