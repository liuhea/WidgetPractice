package com.liuhe.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
		private List<String> datas;

		public MyAdapter(List<String> datas) {
			this.datas = datas;
		}

		@Override
		public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null);
			return new MyViewHolder(view);
		}

		@Override
		public void onBindViewHolder(MyViewHolder holder, int position) {
			holder.tv.setText(datas.get(position));
		}

		@Override
		public int getItemCount() {
			return datas.size();
		}

		class MyViewHolder extends RecyclerView.ViewHolder {
			private TextView tv;

			public MyViewHolder(View itemView) {
				super(itemView);
				tv = (TextView) itemView.findViewById(android.R.id.text1);
			}
		}
	}
