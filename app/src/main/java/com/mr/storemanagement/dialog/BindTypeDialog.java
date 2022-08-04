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
import com.mr.storemanagement.adapter.BindTypeAdapter;

import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/8/4
 * @description:
 */
public class BindTypeDialog extends Dialog implements View.OnClickListener {

    private RecyclerView rvType;

    private BindTypeAdapter bindTypeAdapter;

    private OnSelectTypeListener selectTypeListener;

    public void setSelectTypeListener(OnSelectTypeListener selectTypeListener) {
        this.selectTypeListener = selectTypeListener;
    }

    public BindTypeDialog(@NonNull Context context, List<String> mDataList) {
        super(context, R.style.BottomDialogStyle);
        setContentView(R.layout.dialog_bind_type_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        findViewById(R.id.tv_clear).setOnClickListener(this);
        rvType = findViewById(R.id.rv_type);
        bindTypeAdapter = new BindTypeAdapter(context, mDataList);
        rvType.setLayoutManager(new LinearLayoutManager(context));
        rvType.setAdapter(bindTypeAdapter);

        bindTypeAdapter.setItemClickListener(new BindTypeAdapter.OnTypeItemClickListener() {
            @Override
            public void onClick(String type) {
                if (selectTypeListener != null)
                    selectTypeListener.onSelect(type);
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear:
                dismiss();
                break;
        }
    }

    public interface OnSelectTypeListener {
        void onSelect(String type);
    }

}
