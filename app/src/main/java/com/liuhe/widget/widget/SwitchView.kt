package com.liuhe.widget.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.liuhe.widget.R

/**
 * 组合布局
 * @author liuhe
 * @date 2018-04-03
 */
class SwitchView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    /**
     * 开关是否打开
     */
    var isToggleOn = false
    var toggleView: ImageView

    init {
        // 参数3 root
        // 为null 返回值为参数2填充形成的View 需要addView()
        // 不为null，传入this；参数2形成的View，携带参数布局加入参数3中，返回值为参数2。
        View.inflate(context, R.layout.item_setting, this)

        // 获取用户自定义属性值
        // TypeArray:通过这个类获取自定义属性的值，比如：int、boolean、string
        // 此方法，自定义的属性无法生效。
        // val typeArray = context.obtainStyledAttributes(R.styleable.SwitchView)
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchView)
        // 获取自定义属性值
        // 参数1-自定义属性名在R文件中生成的对应常量
        // 参数2-默认值
        val background = typeArray.getInt(R.styleable.SwitchView_my_background, 0)
        val title = typeArray.getString(R.styleable.SwitchView_my_title)
        val isToggle = typeArray.getBoolean(R.styleable.SwitchView_is_toggle, false)

        setBackgroundResource(when (background) {
            0 -> R.drawable.seting_first_selector
            1 -> R.drawable.seting_middle_selector
            else -> R.drawable.seting_last_selector
        })
        this.findViewById<TextView>(R.id.view_tv_title).text = title ?: "设置"
        toggleView = this.findViewById(R.id.view_iv_toggle)

        setToggle(isToggle)
        // 由于这个类很消耗资源，使用完毕后，必须回收。
        typeArray.recycle()

    }


    fun toggle() {
        setToggle(!isToggleOn)
    }

    private fun setToggle(isToggle: Boolean) {
        toggleView.setImageResource(if (isToggle) R.drawable.on else R.drawable.off)
        isToggleOn = isToggle
    }
}
