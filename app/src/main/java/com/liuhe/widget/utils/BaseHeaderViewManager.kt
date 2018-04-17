package com.liuhe.widget.utils

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.liuhe.widget.R

/**
 *
 * HeaderView管理类
 * @author liuhe
 * @date 2018-04-14
 */
open abstract class BaseHeaderViewManager(var mContext: Context) {

     var headerView: View?=null

    fun getHeaderViewHeight(): Int {
        // 处于视图的加载阶段，并未对视图进行测量，想要获取测量高度，需要提前测量。
        // 当measure(0,0)=measure（0+0，0+0）,即MeasureSpec.UNSPECIFIED,但是控件又不可能为0，那么就会去测量refreshView中具有高度的控件，此时refreshView就被计算了高度
        headerView?.measure(0, 0)
        return headerView?.measuredHeight!!
    }

    abstract fun getHeader(): View?
    abstract fun changeToIdle()

    abstract fun changeToPullDown()
    abstract fun changeToReleaseRefresh()
    abstract fun changeToRefreshing()
    abstract fun endRefreshing()
    abstract fun handleScale(scale: Float)
}