package com.liuhe.widget.activity

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.liuhe.widget.R
import com.liuhe.widget.adapter.ListAdapter
import com.liuhe.widget.constans.targetLayoutIds
import com.liuhe.widget.constans.targetNames

/**
 * @author liuhe
 */
class MainActivity : BaseActivity(), ListAdapter.OnItemClickListener {

    private lateinit var mRcyView: RecyclerView

    override fun getLayoutResID(): Int = R.layout.activity_main

    override fun initData() {
        mRcyView = findViewById(R.id.rcy_main_list)

        mRcyView.layoutManager = LinearLayoutManager(this)
        mRcyView.adapter = ListAdapter(targetNames).apply {
            itemClickListener = this@MainActivity
        }
    }

    override fun onItemClick(position: Int) {
        TargetActivity.start(this@MainActivity, targetLayoutIds[position])
    }
}
