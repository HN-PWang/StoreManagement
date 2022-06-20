package com.mr.storemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;
import com.mr.storemanagement.bean.AsnDetailBean;
import com.mr.storemanagement.bean.PutStorageBean;
import com.mr.storemanagement.bean.StackBean;
import com.mr.storemanagement.util.DataUtil;

import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/6/20
 * @email: 1929774468@qq.com
 * @description:
 */
public class PutStorageDetailAdapter extends RecyclerView.Adapter<PutStorageDetailAdapter.StackViewHolder> {

    private Context mContext;

    private List<AsnDetailBean> mDataList;

    private OnSerialCodeClickListener codeClickListener;

    public void setCodeClickListener(OnSerialCodeClickListener codeClickListener) {
        this.codeClickListener = codeClickListener;
    }

    public PutStorageDetailAdapter(Context context, List<AsnDetailBean> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public StackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_put_storage_detail_layout,
                parent, false);
        return new StackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StackViewHolder holder, int position) {
        holder.itemView.setTag(position);
        AsnDetailBean item = mDataList.get(position);

        holder.tvNo.setText(item.item_Code);
        holder.tvCount.setText(DataUtil.getIntStr(item.finish_qty) + "/" + item.quantity);
        holder.tvSerialCode.setText(item.product_batch);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class StackViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNo;
        public TextView tvCount;
        public TextView tvSerialCode;

        public StackViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNo = itemView.findViewById(R.id.tv_no);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvSerialCode = itemView.findViewById(R.id.tv_serial_code);

            tvSerialCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (codeClickListener != null) {
                        int position = (int) itemView.getTag();
                        codeClickListener.onClick(mDataList.get(position));
                    }
                }
            });
        }
    }

    public interface OnSerialCodeClickListener {
        void onClick(AsnDetailBean bean);
    }

}
