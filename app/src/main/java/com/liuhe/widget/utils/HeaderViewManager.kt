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
 * @author liuhe
 * @date 2018-04-14
 */
class HeaderViewManager(var context: Context) {
    private var headerView: View? = null
    var txtState: TextView? = null
    var imgUpArrow: ImageView? = null
    var imgLoading: ImageView? = null

    private lateinit var upAnim: RotateAnimation
    lateinit var downAnim: RotateAnimation
    lateinit var animationDrawable: AnimationDrawable

    init {
        initAnimation()
    }

    private fun initAnimation() {
        upAnim = RotateAnimation(0f, -180f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        upAnim.duration = 100
        // 动画执行完成后，不会回到原点。
        upAnim.fillAfter = true

        downAnim = RotateAnimation(-180f, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        downAnim.duration = 100
        // 动画执行完成后，不会回到原点。
        downAnim.fillAfter = true
    }

    fun getHeaderView(): View? {
        if (null == headerView) {
            headerView = View.inflate(context, R.layout.view_refresh_header_normal, null)
            txtState = headerView?.findViewById(R.id.tv_normal_refresh_header_status)
            imgUpArrow = headerView?.findViewById(R.id.iv_normal_refresh_header_arrow)
            imgLoading = headerView?.findViewById(R.id.iv_normal_refresh_header_chrysanthemum)
            animationDrawable = imgLoading?.drawable as AnimationDrawable
        }
        return headerView
    }

    fun changeToIdle() {
    }

    fun changeToPullDown() {
        txtState?.text = "下拉刷新"
        imgLoading?.animation = downAnim
    }

    fun changeToReleaseRefresh() {
        txtState?.text = "释放刷新"
        imgUpArrow?.animation = upAnim
    }

    fun changeToRefreshing() {
        txtState?.text = "刷新中"
        // 控件身上执行过动画，需要先清除动画，后设置隐藏才会生效
        imgUpArrow?.clearAnimation()
        imgUpArrow?.visibility = View.INVISIBLE
        imgLoading?.visibility=View.VISIBLE
        animationDrawable.start()
    }
}