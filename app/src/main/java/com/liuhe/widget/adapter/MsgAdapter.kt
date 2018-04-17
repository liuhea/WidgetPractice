package com.liuhe.widget.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.liuhe.widget.R
import com.liuhe.widget.bean.Msg


/**
 * 消息列表适配器
 * @author liuhe
 */
class MsgAdapter(private val context: Context, private val msgList: List<Msg>) : RecyclerView.Adapter<MsgAdapter.MyViewHolder>() {

    var touchListener: OnDragTouchListener = OnDragTouchListener(context)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_rcy, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return msgList?.size
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        holder?.titleView?.text = msgList[position].title
        val unReadMsgCount = msgList[position].unReadMsgCount
        if (position == 0) {
            holder?.unReadMsgCountView?.visibility = View.INVISIBLE
        } else {
            holder?.unReadMsgCountView?.visibility = View.VISIBLE
            holder?.unReadMsgCountView?.text = unReadMsgCount.toString() + ""
        }

        // 监听对应控件的触摸事件
        // 和重写onTouchEvent是有区别的
        // 如果一个控件重写了onTouchEvent并且返回true,但设置了触摸监听返回true,则MotionEvent交给OnTouchListener
        holder?.unReadMsgCountView?.setOnTouchListener(touchListener)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleView: TextView = itemView.findViewById(R.id.tv_title)
        var unReadMsgCountView: TextView = itemView.findViewById(R.id.tv_unReadMsgCount)
    }
}

