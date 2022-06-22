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
import com.mr.storemanagement.adapter.SearchOrderAdapter;
import com.mr.storemanagement.bean.AsnCodeBean;
import com.mr.storemanagement.listener.OnAdapterItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class AsnSelectDialog extends Dialog {

    private RecyclerView rvAsnCode;

    private EditText etSearch;

    private OnOrderSelectListener orderSelectListener;

    private List<AsnCodeBean> showAsnCodeBeans = new ArrayList<>();

    public void setOrderSelectListener(OnOrderSelectListener orderSelectListener) {
        this.orderSelectListener = orderSelectListener;
    }

    public AsnSelectDialog(@NonNull Context context, List<AsnCodeBean> asnCodeBeans) {
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

        showAsnCodeBeans.addAll(asnCodeBeans);

        etSearch = findViewById(R.id.et_search);
        rvAsnCode = findViewById(R.id.tv_order);
        SearchOrderAdapter orderAdapter = new SearchOrderAdapter(getContext(), showAsnCodeBeans);
        rvAsnCode.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAsnCode.setAdapter(orderAdapter);

        etSearch.addTextChangedListener(new AfterTextChangedListener() {
            @Override
            public void afterChanged(Editable editable) {
                String content = editable.toString();
                if (!TextUtils.isEmpty(content)) {
                    showAsnCodeBeans.clear();
                    for (AsnCodeBean bean : asnCodeBeans) {
                        if (bean.asn_code.contains(content)) {
                            showAsnCodeBeans.add(bean);
                        }
                    }
                } else {
                    showAsnCodeBeans.addAll(asnCodeBeans);
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
                if (orderSelectListener != null && asnCodeBeans != null) {
                    orderSelectListener.onSelect(asnCodeBeans.get(position));
                }
                dismiss();
            }
        });
    }

    public interface OnOrderSelectListener {
        void onSelect(AsnCodeBean siteBean);
    }

}
