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
 * @description: 机件号适配器
 */
public class SerialNumAdapter extends RecyclerView.Adapter<SerialNumAdapter.SerialNumViewHolder> {

    private Context mContext;

    private List<String> mDataList;

    public SerialNumAdapter(Context context, List<String> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public SerialNumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_serial_num_layout, parent, false);
        return new SerialNumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SerialNumViewHolder holder, int position) {
        holder.tvSerialCode.setText(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class SerialNumViewHolder extends RecyclerView.ViewHolder {

        public TextView tvSerialCode;

        public SerialNumViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSerialCode = itemView.findViewById(R.id.tv_serial_code);
        }
    }

}
