package com.liuhe.widget.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.umeng.analytics.MobclickAgent

/**
 * Activity基类
 * @author liuhe
 * @date 2018-04-22
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResID())
        initData()
    }

    protected abstract fun initData()

    protected abstract fun getLayoutResID(): Int

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart(packageName)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd(packageName);
    }
}