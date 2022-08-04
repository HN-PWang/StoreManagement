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
 * @date: 2022/8/4
 * @description:
 */
public class BindTypeAdapter extends RecyclerView.Adapter<BindTypeAdapter.TypeViewHolder> {

    private Context mContext;

    private List<String> mDataList;

    private String currentType;

    private OnTypeItemClickListener itemClickListener;

    public void setItemClickListener(OnTypeItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public BindTypeAdapter(Context context, List<String> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_bind_type_layout, parent, false);
        return new TypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        holder.itemView.setTag(position);
        String item = mDataList.get(position);

        holder.tvSerialCode.setText(item);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class TypeViewHolder extends RecyclerView.ViewHolder {

        TextView tvSerialCode;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSerialCode = itemView.findViewById(R.id.tv_serial_code);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) itemView.getTag();
                    if (itemClickListener != null)
                        itemClickListener.onClick(mDataList.get(index));
                }
            });
        }
    }

    public interface OnTypeItemClickListener {
        void onClick(String type);
    }
}
