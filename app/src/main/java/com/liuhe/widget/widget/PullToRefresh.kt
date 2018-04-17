package com.liuhe.widget.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.widget.LinearLayout
import com.liuhe.widget.utils.HeaderViewManager
import com.liuhe.widget.utils.log

/**
 * 万能刷新
 *
 * 1. 三个参数构造函数，直接crash: Caused by: java.lang.NoSuchMethodException: <init> [class android.content.Context, interface android.util.AttributeSet]
 * 2.
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

    var currentState: RefreshState = RefreshState.IDLE
    private lateinit var headerViewManager: HeaderViewManager


    init {
        orientation = VERTICAL
        setupHeaderView()
    }

    private fun setupHeaderView() {
        mHeaderView = LinearLayout(mContext).apply {
            layoutParams = LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            setBackgroundColor(Color.GREEN)
        }
        mHeaderView.gravity = Gravity.CENTER_HORIZONTAL
        addView(mHeaderView)
    }

    fun setupHeaderViewManger() {
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

    private fun handleActionUp(): Boolean {
        when (currentState) {
            RefreshState.PULL_DOWN -> {
                hiddenRefreshView()
                currentState = RefreshState.IDLE
            }
            RefreshState.RELEASE_REFRESH -> {
                currentState = RefreshState.REFRESHING
                changeHeaderViewPaddingTopToZero()
                handleRefreshStatesChanged(currentState)
            }
        }
        // 只要头部拉出一点点，up事件就由当前控件处理
        return mHeaderView.paddingTop > minHeaderViewPaddingTop

    }

    private fun changeHeaderViewPaddingTopToZero() {
        ValueAnimator.ofInt(mHeaderView.paddingTop, 0).apply {
            addUpdateListener {
                //获取值动画在动画变化过程中的值
                mHeaderView.setPadding(0, it.animatedValue as Int, 0, 0)
            }
            duration = 300
        }.start()

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

    private fun handleRefreshStatesChanged(currentState: RefreshState) {
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

    private fun handleActionMove(event: MotionEvent): Boolean {
        val moveY = event.y
        val dy = moveY - downY
        if (dy > 0) {
            var paddingTop = (dy / dragRadio + minHeaderViewPaddingTop).toInt()
            // 阻尼效果：类似于弹簧的效果，随着距离越来越长，拉动越来越难。
            // dy非线性变化即可出现这种效果

            // 判断如果paddingTop>maxHeaderViewPaddingTop，就不能再滑动了
            paddingTop = Math.min(paddingTop, maxHeaderViwPaddingTop)

            // 静止、下拉、释放刷新、刷新状态
            if (paddingTop < 0 && currentState != RefreshState.PULL_DOWN) {
                currentState = RefreshState.PULL_DOWN

                handleRefreshStatesChanged(currentState)
            } else if (paddingTop >= 0 && currentState != RefreshState.RELEASE_REFRESH) {
                currentState = RefreshState.RELEASE_REFRESH
                "释放刷新".log()
            }

            mHeaderView.setPadding(0, paddingTop, 0, 0)
            return true
        }
        return false
    }


    /**
     * 当前控件及子控件全部加载完毕，调用的方法
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        // ViewHeader作为第0个子View
        val contentView = getChildAt(1)
        if (contentView is RecyclerView) {

        }
    }
}

