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
import com.mr.storemanagement.bean.ContainerGoodsBean;
import com.mr.storemanagement.util.DataUtil;

import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/6/21
 * @email: 1929774468@qq.com
 * @description: 出库拣货商品列表
 */
public class OutStockGoodsAdapter extends RecyclerView.Adapter<OutStockGoodsAdapter.GoodsViewHolder> {

    private Context mContext;

    private List<ContainerGoodsBean> mDataList;

    private OnSerialCodeClickListener serialCodeClickListener;

    public void setSerialCodeClickListener(OnSerialCodeClickListener serialCodeClickListener) {
        this.serialCodeClickListener = serialCodeClickListener;
    }

    public OutStockGoodsAdapter(Context context, List<ContainerGoodsBean> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public GoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_out_stock_goods_layout,
                parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsViewHolder holder, int position) {
        holder.itemView.setTag(position);
        ContainerGoodsBean item = mDataList.get(position);

        holder.tvNo.setText(item.item_Code);
        holder.tvCount.setText(DataUtil.getIntStr(item.checkQty) + "/" + item.request_Qty);
        holder.tvSerialCode.setText(item.product_batch);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNo;
        public TextView tvCount;
        public TextView tvSerialCode;

        public GoodsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNo = itemView.findViewById(R.id.tv_no);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvSerialCode = itemView.findViewById(R.id.tv_serial_code);

            tvSerialCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (serialCodeClickListener != null) {
                        int position = (int) itemView.getTag();
                        serialCodeClickListener.onClick(mDataList.get(position));
                    }
                }
            });
        }
    }

    public interface OnSerialCodeClickListener {
        void onClick(ContainerGoodsBean bean);
    }
}
