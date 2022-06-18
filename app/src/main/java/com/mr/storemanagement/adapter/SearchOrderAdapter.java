package com.mr.storemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.OrderBean;
import com.mr.storemanagement.listener.OnAdapterItemClickListener;

import java.util.List;


public class SearchOrderAdapter extends RecyclerView.Adapter<SearchOrderAdapter.OrderViewHolder> {

    private Context mContext;

    private List<OrderBean> mDataList;

    private OnAdapterItemClickListener itemClickListener;

    public void setItemClickListener(OnAdapterItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SearchOrderAdapter(Context context, List<OrderBean> dateList) {
        this.mContext = context;
        this.mDataList = dateList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_order_layout,
                parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderBean item = mDataList.get(position);

        holder.tvName.setText(item.asn_code);

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_order_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) itemView.getTag();
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(position);
                }
            });
        }
    }

}
