package com.mr.storemanagement.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.SearchOrderAdapter;
import com.mr.storemanagement.bean.OrderBean;
import com.mr.storemanagement.listener.OnAdapterItemClickListener;

import java.util.List;


public class OrderNoSelectDialog extends Dialog {

    private RecyclerView rvOrder;

    private OnOrderSelectListener orderSelectListener;

    public void setOrderSelectListener(OnOrderSelectListener orderSelectListener) {
        this.orderSelectListener = orderSelectListener;
    }

    public OrderNoSelectDialog(@NonNull Context context, List<OrderBean> orderBeans) {
        super(context, R.style.BottomDialogStyle);
        setContentView(R.layout.dialog_order_no_select_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        rvOrder = findViewById(R.id.tv_order);
        SearchOrderAdapter orderAdapter = new SearchOrderAdapter(getContext(), orderBeans);
        rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrder.setAdapter(orderAdapter);

        findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        orderAdapter.setItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (orderSelectListener != null && orderBeans != null) {
                    orderSelectListener.onSelect(orderBeans.get(position));
                }
                dismiss();
            }
        });

    }

    public interface OnOrderSelectListener {
        void onSelect(OrderBean siteBean);
    }

}
