package com.liuhe.widget.utils

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import com.liuhe.widget.R

/**
 *
 * 美团HeaderView
 *  1. 缩放阶段：PULL_DOWN
 *  2. 释放阶段：RELEASE_REFRESH
 *  3. 刷新阶段：REFRESHING
 * @author liuhe
 * @date 2018-04-14
 */
class MTHeaderViewManager(var context: Context) : BaseHeaderViewManager(context) {

    private var mDownView: ImageView? = null
    private var mRefreshingView: ImageView? = null
    var releaseAnim: AnimationDrawable? = null
    var refreshingAnim: AnimationDrawable? = null

    override fun getHeader(): View? {
        if (null == headerView) {
            headerView = View.inflate(context, R.layout.view_refresh_header_meituan, null)
            mDownView = headerView?.findViewById(R.id.iv_meituan_pull_down)
            mRefreshingView = headerView?.findViewById(R.id.iv_meituan_release_refreshing)
        }
        return headerView
    }

    override fun changeToIdle() {
        // 收尾工作
        mDownView?.visibility = View.VISIBLE
        mRefreshingView?.visibility = View.INVISIBLE
        // 如果网络请求速度超过动画执行时间，动画可能停止不了
        refreshingAnim?.stop()
        releaseAnim?.stop()
    }

    override fun changeToPullDown() {
        mDownView?.visibility = View.VISIBLE
        mRefreshingView?.visibility = View.INVISIBLE
    }

    override fun changeToReleaseRefresh() {
        mDownView?.visibility = View.INVISIBLE
        mRefreshingView?.visibility = View.VISIBLE
        mRefreshingView?.setImageResource(R.drawable.release_refresh)

        releaseAnim = mRefreshingView?.drawable as AnimationDrawable
        releaseAnim?.start()
    }

    override fun changeToRefreshing() {
        mRefreshingView?.setImageResource(R.drawable.refresh_mt_refreshing)
        refreshingAnim = mRefreshingView?.drawable as AnimationDrawable
        refreshingAnim?.start()
    }


    override fun endRefreshing() {
        mDownView?.visibility = View.INVISIBLE
        mRefreshingView?.visibility = View.INVISIBLE
    }


    override fun handleScale(scale: Float) {
        mDownView?.scaleX = scale
        mDownView?.scaleY = scale
    }
}