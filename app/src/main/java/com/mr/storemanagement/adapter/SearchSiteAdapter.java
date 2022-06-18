package com.mr.storemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.SiteBean;
import com.mr.storemanagement.listener.OnAdapterItemClickListener;

import java.util.List;


public class SearchSiteAdapter extends RecyclerView.Adapter<SearchSiteAdapter.SiteViewHolder> {

    private Context mContext;

    private List<SiteBean> mDataList;

    private OnAdapterItemClickListener itemClickListener;

    public void setItemClickListener(OnAdapterItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SearchSiteAdapter(Context context, List<SiteBean> dateList) {
        this.mContext = context;
        this.mDataList = dateList;
    }

    @NonNull
    @Override
    public SiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_site_layout,
                parent, false);
        return new SiteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SiteViewHolder holder, int position) {
        SiteBean item = mDataList.get(position);

        holder.tvName.setText(item.site_code);

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class SiteViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public SiteViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_site_name);

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
