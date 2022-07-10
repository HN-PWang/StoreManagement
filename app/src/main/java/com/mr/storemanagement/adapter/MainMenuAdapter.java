package com.mr.storemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.MainMenuBean;

import java.util.List;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.MenuViewHolder> {

    private Context mContext;

    private List<MainMenuBean> mDataList;

    private OnMenuClickListener onMenuClickListener;

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    public MainMenuAdapter(Context context, List<MainMenuBean> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_main_menu_layout, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.itemView.setTag(position);
        MainMenuBean item = mDataList.get(position);

        holder.ivIcon.setImageResource(item.icon);
        holder.tvMenu.setText(item.name);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivIcon;
        public TextView tvMenu;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMenu = itemView.findViewById(R.id.tv_menu);
            ivIcon = itemView.findViewById(R.id.iv_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) itemView.getTag();
                    if (onMenuClickListener != null) {
                        onMenuClickListener.onClick(mDataList.get(position));
                    }
                }
            });
        }
    }

    public interface OnMenuClickListener {
        void onClick(MainMenuBean item);
    }

}
