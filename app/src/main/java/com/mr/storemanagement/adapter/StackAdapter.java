package com.mr.storemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.StackBean;
import com.mr.storemanagement.util.DataUtil;

import java.util.List;

public class StackAdapter extends RecyclerView.Adapter<StackAdapter.StackViewHolder> {

    private Context mContext;

    private List<StackBean> mDataList;

    public StackAdapter(Context context, List<StackBean> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public StackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_common_stock_detail_layout,
                parent, false);
        return new StackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StackViewHolder holder, int position) {
        StackBean item = mDataList.get(position);

        holder.tvItemCode.setText("册序号："+item.item_Code);
        holder.tvContainerNo.setText("容器号："+item.container_code);
        holder.tvCountTag.setText("可用数/系统数：");
        holder.tvCount.setText(DataUtil.getInt(item.available_qty) + "/" + DataUtil.getInt(item.real_qty));
        holder.tvSerialCode.setText(item.product_batch);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class StackViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItemCode;
        public TextView tvContainerNo;
        public TextView tvCountTag;
        public TextView tvCount;
        public TextView tvSerialCode;

        public StackViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemCode = itemView.findViewById(R.id.tv_item_code);
            tvContainerNo = itemView.findViewById(R.id.tv_container_no);
            tvCountTag = itemView.findViewById(R.id.tv_count_tag);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvSerialCode = itemView.findViewById(R.id.tv_serial_code);
        }

    }
}
