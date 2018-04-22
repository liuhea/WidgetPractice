package com.liuhe.widget.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @author liuhe
 */
class ListAdapter(private val data: List<String>) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, null)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv.text = data[position]
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = data?.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv: TextView = itemView.findViewById(android.R.id.text1)
    }

    interface OnItemClickListener {
        /**
         * @param position
         */
        fun onItemClick(position: Int)
    }
}
