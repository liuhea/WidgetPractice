package com.liuhe.widget.utils

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast

/**
 *
 * @author liuhe
 * @date 2018-04-08
 */

/**
 * 获取状态栏高度
 */
fun getStatusBarHeight(view: View): Int {
    val rect = Rect()
    // 获取视图相应的可视范围，会把视图的左上右下的数据传入到一个矩形中
    view.getWindowVisibleDisplayFrame(rect)
    return rect.top
}


/**
 * Created by liuhe on 20/03/2018.
 */

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    if (TextUtils.isEmpty(msg)) {
        return
    }
    Toast.makeText(this.applicationContext, msg, duration).show()
}

fun String.log(logType: Int = Log.DEBUG) {
    if (TextUtils.isEmpty(this)) {
        return
    }
    when (logType) {
        Log.ERROR -> Log.e("liuhe->e", this)
        else -> Log.d("liuhe->d", this)
    }
}

/**
 *  屏幕宽（像素，如：3200px）
 */
fun getScreenWidth(context: Context): Float {
    val appContext = context.applicationContext
    var dm = appContext.resources.displayMetrics as DisplayMetrics
    return dm.widthPixels.toFloat()
}

/**
 * 屏幕高（像素，如：1280px）
 */
fun getScreenHeight(context: Context): Float {
    val appContext = context.applicationContext
    var dm = appContext.resources.displayMetrics as DisplayMetrics
    return dm.heightPixels.toFloat()
}

/**
 * 屏幕密度（每寸像素：120/160/240/320）
 */
fun getScreenDPI(context: Context): Float {
    val appContext = context.applicationContext
    var dm = appContext.resources.displayMetrics as DisplayMetrics
    return dm.densityDpi.toFloat()
}

/**
 *  屏幕密度（像素比例：0.75/1.0/1.5/2.0）
 */
fun getScreenDensity(context: Context): Float {
    val appContext = context.applicationContext
    var dm = appContext.resources.displayMetrics as DisplayMetrics
    return dm.density
}
