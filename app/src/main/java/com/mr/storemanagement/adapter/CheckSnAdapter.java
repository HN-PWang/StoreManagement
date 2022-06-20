package com.mr.storemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;

import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/6/20
 * @email: 1929774468@qq.com
 * @description:
 */
public class CheckSnAdapter extends RecyclerView.Adapter<CheckSnAdapter.SnCodeViewHolder> {

    private Context mContext;

    private List<String> mDataList;

    public CheckSnAdapter(Context context, List<String> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public SnCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_serial_num_layout, parent, false);
        return new SnCodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SnCodeViewHolder holder, int position) {
        holder.tvSnCode.setText(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class SnCodeViewHolder extends RecyclerView.ViewHolder {

        public TextView tvSnCode;

        public SnCodeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSnCode = itemView.findViewById(R.id.tv_sn_code);
        }
    }

}
