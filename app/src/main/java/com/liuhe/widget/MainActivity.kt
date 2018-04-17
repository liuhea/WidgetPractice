package com.liuhe.widget

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.animation.RotateAnimation
import com.liuhe.widget.adapter.MsgAdapter
import com.liuhe.widget.adapter.MyAdapter
import com.liuhe.widget.bean.Msg
import com.liuhe.widget.bean.PieData
import com.liuhe.widget.utils.MTHeaderViewManager
import com.liuhe.widget.utils.log
import com.liuhe.widget.widget.PullToRefresh
import kotlinx.android.synthetic.main.activity_circle_menu.*
import kotlinx.android.synthetic.main.activity_drag_circle.*
import kotlinx.android.synthetic.main.activity_pie.*
import kotlinx.android.synthetic.main.activity_pull_refresh.*
import kotlinx.android.synthetic.main.activity_rcy.*
import kotlinx.android.synthetic.main.activity_switch.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_refresh)
        "onCreate()执行完毕".log()

        mockPieData()

        mockCircleMenuData()

        mockSwitch()

        mockDragCircle()

        mockRcy()

        mockPullRefresh()

        mockAnimation()

    }

    private fun mockAnimation() {
        /**
         * @param fromDegress 开始角度
         * @param toDEgress 结束角度
         * 后边四个参数确定中心点
         */
        var downAnim = RotateAnimation(-180f, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        downAnim.duration = 600
        // 动画执行完成后，会回到原点。
        downAnim.fillAfter = false

        iv_refresh_arrow.setOnClickListener {
            iv_refresh_arrow.startAnimation(downAnim)
        }
    }

    private fun mockPullRefresh() {
        pull_to_refresh?.initHeaderViewManager(MTHeaderViewManager(this))
        var rcyData = mutableListOf<String>()
        for (i in 0..20) {
            rcyData.add("标题$i")
        }
        rcy_pull_main?.layoutManager = LinearLayoutManager(this)
        rcy_pull_main?.adapter = MyAdapter(rcyData)

        val handler = Handler()
        pull_to_refresh.setRefreshingListener(object : PullToRefresh.OnRefreshingListener {
            override fun onRefresh() {
                handler.postDelayed({
                    pull_to_refresh.endRefreshing()
                }, 2000)
            }
        })
    }


    private fun mockDragCircle() {
        drag_circle?.setText(2)
        drag_circle?.setDragViewPosition(450f, 450f)
    }

    /**
     * 消息列表
     */
    private fun mockRcy() {
        var rcyData = mutableListOf<Msg>()
        for (i in 0..20) {
            rcyData.add(Msg("标题$i", i))
        }
        rcy_main?.layoutManager = LinearLayoutManager(this)
        rcy_main?.adapter = MsgAdapter(this, rcyData)
    }

    private fun mockSwitch() {

        switch_main?.setOnClickListener {
            switch_main.toggle()
        }
    }

    /**
     * 模拟圆形按钮数据
     */
    private fun mockCircleMenuData() {
        val texts = listOf("安全中心 ", "特色服务", "投资理财",
                "转账汇款", "我的账户", "信用卡")
        val imgIds = listOf(R.drawable.home_mbank_1_normal,
                R.drawable.home_mbank_2_normal, R.drawable.home_mbank_3_normal,
                R.drawable.home_mbank_4_normal, R.drawable.home_mbank_5_normal,
                R.drawable.home_mbank_6_normal)
        circle_menu?.setData(texts, imgIds)
    }

    /**
     * 模拟扇形数据
     */
    private fun mockPieData() {
        var pies = mutableListOf<PieData>()
        val colors = listOf(0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
                0xFFE6B800, 0xFF7CFC00)
        for (i in 0 until colors.size) {
            pies.add(PieData((i + 1).toFloat(), colors[i]))
        }
        pie_main?.setData(pies)
    }


    override fun onStart() {
        super.onStart()
        "onStart()执行完毕".log()
    }

    override fun onResume() {
        super.onResume()
        "onResume()执行完毕".log()
    }

    override fun onPause() {
        super.onPause()
        "onPause()执行完毕".log()
    }

    override fun onStop() {
        super.onStop()
        "onStop()执行完毕".log()
    }

    override fun onDestroy() {
        super.onDestroy()
        "onDestroy()执行完毕".log()
    }
}
