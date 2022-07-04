package com.mr.storemanagement.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mr.lib_base.AfterTextChangedListener;
import com.mr.storemanagement.R;
import com.mr.storemanagement.adapter.SearchInvAdapter;
import com.mr.storemanagement.adapter.SearchOrderAdapter;
import com.mr.storemanagement.bean.AsnCodeBean;
import com.mr.storemanagement.bean.InvCodeBean;
import com.mr.storemanagement.listener.OnAdapterItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class InvSelectDialog extends Dialog {

    private RecyclerView rvAsnCode;

    private EditText etSearch;

    private OnOrderSelectListener orderSelectListener;

    private List<InvCodeBean> showAsnCodeBeans = new ArrayList<>();

    public void setOrderSelectListener(OnOrderSelectListener orderSelectListener) {
        this.orderSelectListener = orderSelectListener;
    }

    public InvSelectDialog(@NonNull Context context, List<InvCodeBean> invCodeBeans) {
        super(context, R.style.BottomDialogStyle);
        setContentView(R.layout.dialog_inv_select_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        showAsnCodeBeans.addAll(invCodeBeans);

        etSearch = findViewById(R.id.et_search);
        rvAsnCode = findViewById(R.id.tv_order);
        SearchInvAdapter orderAdapter = new SearchInvAdapter(getContext(), showAsnCodeBeans);
        rvAsnCode.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAsnCode.setAdapter(orderAdapter);

        etSearch.addTextChangedListener(new AfterTextChangedListener() {
            @Override
            public void afterChanged(Editable editable) {
                String content = editable.toString();
                if (!TextUtils.isEmpty(content)) {
                    showAsnCodeBeans.clear();
                    for (InvCodeBean bean : invCodeBeans) {
                        if (bean.inventory_code.contains(content)) {
                            showAsnCodeBeans.add(bean);
                        }
                    }
                } else {
                    showAsnCodeBeans.addAll(invCodeBeans);
                }
                orderAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        orderAdapter.setItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (orderSelectListener != null && invCodeBeans != null) {
                    orderSelectListener.onSelect(invCodeBeans.get(position));
                }
                dismiss();
            }
        });
    }

    public interface OnOrderSelectListener {
        void onSelect(InvCodeBean siteBean);
    }

}
