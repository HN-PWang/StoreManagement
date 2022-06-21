package com.mr.storemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.GetTaskBean;

import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/6/21
 * @email: 1929774468@qq.com
 * @description:
 */
public class GetTaskAdapter extends RecyclerView.Adapter<GetTaskAdapter.TaskViewHolder> {

    private Context mContext;

    private List<GetTaskBean> mDataList;

    public GetTaskAdapter(Context context, List<GetTaskBean> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_get_stak_layout,
                parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.itemView.setTag(position);
        GetTaskBean item = mDataList.get(position);

        holder.tvNo.setText(item.no);
        holder.tvStartLocation.setText(item.start_location);
        holder.tvCurrentLocation.setText(item.current_location);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNo;
        public TextView tvStartLocation;
        public TextView tvCurrentLocation;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNo = itemView.findViewById(R.id.tv_no);
            tvStartLocation = itemView.findViewById(R.id.tv_start_location);
            tvCurrentLocation = itemView.findViewById(R.id.tv_current_location);

        }
    }
}
