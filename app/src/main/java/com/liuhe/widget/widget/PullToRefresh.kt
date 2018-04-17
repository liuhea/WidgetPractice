package com.liuhe.widget.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.ScrollView
import com.liuhe.widget.utils.HeaderViewManager
import com.liuhe.widget.utils.log
import com.liuhe.widget.utils.RefreshScrollingUtil


/**
 * 万能刷新
 *
 * 1. 三个参数构造函数，直接crash: Caused by: java.lang.NoSuchMethodException: <init> [class android.content.Context, interface android.util.AttributeSet]
 * @author liuhe
 * @date 2018-04-14
 */
class PullToRefresh @JvmOverloads constructor(private var mContext: Context, attrs: AttributeSet? = null) : LinearLayout(mContext, attrs) {

    val TAG = "PullToRefresh"

    private lateinit var mHeaderView: LinearLayout

    /**
     * 头部视图的最小上边距
     */
    private var minHeaderViewPaddingTop = 0
    /**
     * 头部视图的最大上边距=头部视图的高度*都不
     */
    private var maxHeaderViwPaddingTop = 0

    /**
     * 头部视图的最大上边距系数
     */
    private var maxHeaderViwPaddingTopRadio = 0.3f

    /**
     * 阻尼系数
     */
    private val dragRadio = 1.8f

    /**
     *
     */
    private var downY = 0f

    /**
     * 静止、下拉、释放刷新、刷新状态
     * (1)枚举不占用任何实际的值
     * (2)枚举中的量都是常量
     * (3)枚举一旦定义完，只能使用枚举中的常量。
     */
    enum class RefreshState {
        IDLE, PULL_DOWN, RELEASE_REFRESH, REFRESHING
    }

    private var currentState: RefreshState = RefreshState.IDLE
    private lateinit var headerViewManager: HeaderViewManager

    private var interceptDownX: Float = 0f
    private var interceptDownY: Float = 0f

    /**
     * 子View,用来判断是否滑动到子View顶部位置
     */
    private var contentView: View? = null

    init {
        orientation = VERTICAL
        initHeaderView()
    }

    private fun initHeaderView() {
        mHeaderView = LinearLayout(mContext).apply {
            layoutParams = LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            setBackgroundColor(Color.GREEN)
        }
        mHeaderView.gravity = Gravity.CENTER_HORIZONTAL
        addView(mHeaderView)
    }

    /**
     * 初始化header头管理器
     */
    fun initHeaderViewManager() {
        headerViewManager = HeaderViewManager(mContext)
        val refreshView = headerViewManager.getHeaderView()
        // 处于视图的加载阶段，并未对视图进行测量，想要获取测量高度，需要提前测量。
        // 当measure(0,0)=measure（0+0，0+0）,即MeasureSpec.UNSPECIFIED,但是控件又不可能为0，那么就会去测量refreshView中具有高度的控件，此时refreshView就被计算了高度
        refreshView?.measure(0, 0)
        minHeaderViewPaddingTop = -refreshView?.measuredHeight!!
        maxHeaderViwPaddingTop = (refreshView.measuredHeight * maxHeaderViwPaddingTopRadio).toInt()

        mHeaderView.setPadding(0, minHeaderViewPaddingTop, 0, 0)
        mHeaderView.addView(refreshView)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                return handleActionMove(event)
            }
            MotionEvent.ACTION_UP -> {
                handleActionUp()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleActionMove(event: MotionEvent): Boolean {
        val moveY = event.y
        val dy = moveY - downY

        "$TAG moveY=$moveY dy=$dy".log()
        /*
        向下滑动瞬间跳出头部视图
        由于down事件被RecyclerView消费掉,当RefreshLayout拿到事件已经变为move了,故:downY无法获取到值,应该在handleActionMove方法中给downY继续赋值
         */
        if (downY == 0f) {
            downY = event.y
        }
        if (dy > 0) {
            var paddingTop = (dy / dragRadio + minHeaderViewPaddingTop).toInt()
            // 阻尼效果：类似于弹簧的效果，随着距离越来越长，拉动越来越难。
            // dy非线性变化即可出现这种效果

            // 静止、下拉、释放刷新、刷新状态
            if (paddingTop < 0 && currentState != RefreshState.PULL_DOWN) {
                currentState = RefreshState.PULL_DOWN
                "$TAG 下拉刷新".log()
                handleRefreshStatesChanged()

            } else if (paddingTop >= 0 && currentState != RefreshState.RELEASE_REFRESH) {
                currentState = RefreshState.RELEASE_REFRESH
                "$TAG 释放刷新".log()
                handleRefreshStatesChanged()
            }
            // 判断如果paddingTop>maxHeaderViewPaddingTop，就不能再滑动了
            paddingTop = Math.min(paddingTop, maxHeaderViwPaddingTop)
            //不断的设置paddingTop,来达到将头部拉出的目的
            mHeaderView.setPadding(0, paddingTop, 0, 0)
            return true
        }
        return false
    }


    private fun handleActionUp(): Boolean {
        "$TAG $currentState".log()
        /*
        * 第一次滑动后抬起,第二次滑动的down的位置向上偏移,此时向下滑动,一段距离没有任何反应,直到第一次滑动的down的位置才有效果
        * 原因:由于第二次滑动时,downY采用上一次的downY造成
         */
        downY = 0f

        when (currentState) {
            RefreshState.PULL_DOWN -> {
                hiddenRefreshView()
                currentState = RefreshState.IDLE
            }
            RefreshState.RELEASE_REFRESH -> {
                currentState = RefreshState.REFRESHING
                changeHeaderViewPaddingTopToZero()
                handleRefreshStatesChanged()
            }
        }
        // 只要头部拉出一点点，up事件就由当前控件处理
        return mHeaderView.paddingTop > minHeaderViewPaddingTop

    }

    /**
     * 处理刷新状态
     */
    private fun handleRefreshStatesChanged() {
        when (currentState) {
            RefreshState.IDLE -> {
                headerViewManager.changeToIdle()
            }
            RefreshState.PULL_DOWN -> {
                headerViewManager.changeToPullDown()
            }
            RefreshState.RELEASE_REFRESH -> {
                headerViewManager.changeToReleaseRefresh()
            }
            RefreshState.REFRESHING -> {
                headerViewManager.changeToRefreshing()
            }
        }
    }

    private fun changeHeaderViewPaddingTopToZero() {
        postDelayed({ hiddenRefreshView() }, 350)
//        ValueAnimator.ofInt(mHeaderView.paddingTop, minHeaderViewPaddingTop).apply {
//            addUpdateListener {
//                //获取值动画在动画变化过程中的值
//                mHeaderView.setPadding(0, it.animatedValue as Int, 0, 0)
//            }
//            duration = 600
//        }.start()
    }

    private fun hiddenRefreshView() {
        ValueAnimator.ofInt(mHeaderView.paddingTop, minHeaderViewPaddingTop).apply {
            addUpdateListener {
                //获取值动画在动画变化过程中的值
                mHeaderView.setPadding(0, it.animatedValue as Int, 0, 0)
            }
            duration = 300
        }.start()
    }


    /**
     * 处理ContentView是RecyclerView等情况
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                interceptDownX = ev.x
                interceptDownY = ev.y
            }

            MotionEvent.ACTION_MOVE -> {
                val dY = ev.y - interceptDownY
                //1,y方向的变化:y方向的变化量>x方向的变化量
                if (Math.abs(ev.x - interceptDownX) < Math.abs(dY)) {
                    //2,y方向向下滑动
                    if (dY > 0 && handleScrollTop()) {
                        return true
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
            }
        }
        return super.onInterceptTouchEvent(ev)
    }


    /**
     * 当前控件及子控件全部加载完毕，调用的方法
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        // ViewHeader作为第0个子View
        contentView = getChildAt(1)

    }

    /**
     * 判断子视图是否滑动到屏幕顶部
     */
    private fun handleScrollTop(): Boolean {
        return when (contentView) {
            is RecyclerView -> RefreshScrollingUtil.isRecyclerViewToTop(contentView as RecyclerView)
            is AbsListView -> RefreshScrollingUtil.isAbsListViewToTop(contentView as AbsListView)
            is ScrollView -> RefreshScrollingUtil.isScrollViewOrWebViewToTop(contentView)
            else -> false
        }
    }
}


