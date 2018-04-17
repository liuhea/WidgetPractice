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
class HeaderViewManager(var context: Context) {

    private var headerView: View? = null
    private var mStateTxt: TextView? = null
    private var mArrowView: ImageView? = null
    private var mLoadingView: ImageView? = null
    private var mLoadingAnimDrawable: AnimationDrawable? = null

    private lateinit var mUpAnim: RotateAnimation
    private lateinit var mDownAnim: RotateAnimation

    init {
        initAnimation()
    }

    private fun initAnimation() {

        mDownAnim = RotateAnimation(-180f, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        mDownAnim.duration = 100
        // 动画执行完成后，不会回到原点。
        mDownAnim.fillAfter = true

        mUpAnim = RotateAnimation(0f, -180f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        mUpAnim.duration = 100
        // 动画执行完成后，不会回到原点。
        mUpAnim.fillAfter = true
    }

    fun getHeaderView(): View? {
        if (null == headerView) {
            headerView = View.inflate(context, R.layout.view_refresh_header_normal, null)
            mStateTxt = headerView?.findViewById(R.id.txt_refresh_header_status)
            mArrowView = headerView?.findViewById(R.id.iv_refresh_header_arrow)
            mLoadingView = headerView?.findViewById(R.id.iv_refresh_header_loading)

            mLoadingAnimDrawable = mLoadingView?.drawable as AnimationDrawable
        }
        return headerView
    }

    fun changeToIdle() {
    }

    fun changeToPullDown() {
        mStateTxt?.text = "下拉刷新"
        mArrowView?.startAnimation(mDownAnim)
        mLoadingView?.visibility = View.INVISIBLE

    }

    fun changeToReleaseRefresh() {
        mStateTxt?.text = "释放刷新"
        mArrowView?.startAnimation(mUpAnim)
        mLoadingView?.visibility = View.INVISIBLE
    }

    fun changeToRefreshing() {
        mStateTxt?.text = "刷新中"
        // 控件身上执行过动画，需要先清除动画，后设置隐藏才会生效
        mArrowView?.clearAnimation()
        mArrowView?.visibility = View.INVISIBLE

        mLoadingView?.visibility = View.VISIBLE
        mLoadingAnimDrawable?.start()
    }


    fun endRefreshing() {}


    fun handleScale(scale: Float) {}
}