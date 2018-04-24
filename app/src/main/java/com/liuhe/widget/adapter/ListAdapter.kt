package com.liuhe.widget.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.liuhe.widget.R

/**
 * @author liuhe
 */
class ListAdapter(private val data: List<String>) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_adapter, null)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            tv.text = data[position]
            tv.setOnClickListener {
                itemClickListener?.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int = data?.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv: TextView = itemView.findViewById(R.id.tv_adapter)
    }

    interface OnItemClickListener {
        /**
         * @param position
         */
        fun onItemClick(position: Int)
    }
}
