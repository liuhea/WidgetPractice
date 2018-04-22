package com.liuhe.widget.constans

import com.liuhe.widget.R
import com.liuhe.widget.widget.*

/**
 *
 * @author liuhe
 * @date 2018-04-22
 */

val targetNames: List<String> = listOf(CanvasView::class.java.simpleName, CircleMenuView::class.java.simpleName,
        DragCircleView::class.java.simpleName, MyViewGroup::class.java.simpleName,
        PathView::class.java.simpleName, PieView::class.java.simpleName,
        PullToRefresh::class.java.simpleName, SwitchView::class.java.simpleName
)

var targetLayoutIds = listOf(R.layout.activity_canvas, R.layout.activity_circle_menu,
        R.layout.activity_drag_circle, R.layout.activity_viewgroup,
        R.layout.activity_path, R.layout.activity_pie,
        R.layout.activity_pull_refresh, R.layout.activity_switch)


const val BUNDLE_KEY_LAYOUTID="BUNDLE_KEY_layoutId"